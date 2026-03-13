package com.calia;

import com.calia.object.Camera;
import com.calia.object.Entity;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public abstract class CaliaBase extends CoreBase {
    final int WORLD_WIDTH;
    final int WORLD_HEIGHT;

    public CaliaBase(String title, int worldWidth, int worldHeight) {
        super(title);
        this.WORLD_WIDTH = worldWidth;
        this.WORLD_HEIGHT = worldHeight;
        WORLD_HALF_WIDTH = WORLD_WIDTH / 2.0;
        MIN_X = -WORLD_HALF_WIDTH;
        MAX_X = WORLD_HALF_WIDTH;

        WORLD_HALF_HEIGHT = WORLD_HEIGHT / 2.0;
        MIN_Y = -WORLD_HALF_HEIGHT;
        MAX_Y = WORLD_HALF_HEIGHT;
    }

    final double cullingDistanceWidth = 2000;
    final double cullingDistanceHeight = 1000;

    final int VIRTUAL_X_SCREEN_CENTER = 960;
    final int VIRTUAL_Y_SCREEN_CENTER = 540;

    final double WORLD_HALF_WIDTH;
    public final double MIN_X;
    public final double MAX_X;

    final double WORLD_HALF_HEIGHT;
    public final double MIN_Y;
    public final double MAX_Y;

    protected abstract void update(double deltaTime);
    protected abstract void init();
    protected void clickEvent() {}
    protected final void exit() { internalExit(); }

    private boolean isEntitiesNeedSort = false;
    private ArrayList<Entity> entities = new ArrayList<>();

    private final Camera camera = new Camera();

    private boolean hitBoxRender = false;
    private boolean pause = false;
    private boolean launch = false;

    protected void addEntity(Entity e) {
        entities.add(e);
        isEntitiesNeedSort = true;
    }

    @Override
    protected final void internalInit() {
        init();
    }

    @Override
    protected final void internalUpdate(double dt) {
        if (!launch) return;
        if (pause) return;
        update(dt);
        //internal update
        for (int i = entities.size() - 1; i >= 0; i--) {
            Entity e = entities.get(i);
            e.update(dt);
            e.EntityUpdate(dt);
            e.checkBound(this);
            if (e.isRemove()) {
                entities.remove(i);
            }
        }
        if (isEntitiesNeedSort) {
            entities.sort(Comparator.comparingInt(Entity::getLayer).reversed());
            isEntitiesNeedSort = false;
        }
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (!e.isCollisionEnabled()) continue;

            for (int j = i + 1; j < entities.size(); j++) { // i + 1부터 시작하는 게 핵심!
                Entity e2 = entities.get(j);
                if (e2.isCollisionEnabled()) {
                    resolveCollision(e, e2);
                }
            }
        }
    }

    /**
     * Renders the background elements behind entities.
     */
    protected void backGroundRender(Graphics g) {}

    /**
     * Renders at the top-most layer, just below the HUD.
     */
    protected void render(Graphics g) {}

    @Override
    protected final void internalRender(Graphics g) {
        if (!launch) return;
        backGroundRender(g);
        for (int i = entities.size() - 1; i >= 0; i--) {
            Entity e = entities.get(i);
            double relativeX = e.getX() - camera.getX();
            double relativeY = e.getY() - camera.getY();

            if (cullingDistanceWidth > relativeX) {
                if (-1.0 * cullingDistanceWidth < relativeX) {
                    if (cullingDistanceHeight > relativeY) {
                        if (-1.0 * cullingDistanceHeight < relativeY) {
                            e.render(g,e.getX() - camera.getX() + VIRTUAL_X_SCREEN_CENTER,e.getY() - camera.getY() + VIRTUAL_Y_SCREEN_CENTER);
                            if (hitBoxRender) e.renderHitbox(g,e.getX() - camera.getX() + VIRTUAL_X_SCREEN_CENTER,e.getY() - camera.getY() + VIRTUAL_Y_SCREEN_CENTER);
                        }
                    }
                }
            }
        }
        render(g);
    }

    @Override
    protected void internalClick() {
        clickEvent();
    }

    protected final void launch() {launch = true;}

    public void resolveCollision(Entity a, Entity b) {
        double dx = b.getX() - a.getX();
        double dy = b.getY() - a.getY();
        double distSq = dx * dx + dy * dy;
        double radiusSum = a.getRadius() + b.getRadius();

        if (distSq < radiusSum * radiusSum) {
            double distance = Math.sqrt(distSq);
            if (distance == 0) distance = 0.01;

            double overlap = radiusSum - distance;
            double nx = dx / distance; // 충돌 법선 벡터 x
            double ny = dy / distance; // 충돌 법선 벡터 y

            // 1. 질량 정보 가져오기 (config에 선언되어 있다고 가정)
            double massA = a.getConfig().getMass();
            double massB = b.getConfig().getMass();
            double totalMass = massA + massB;

            // 2. 위치 보정 (겹침 해결) - 질량 비율에 따라 밀어내기
            // 질량이 클수록 적게 밀림
            double ratioA = massB / totalMass;
            double ratioB = massA / totalMass;

            a.addX(-nx * overlap * ratioA);
            a.addY(-ny * overlap * ratioA);
            b.addX(nx * overlap * ratioB);
            b.addY(ny * overlap * ratioB);

            // 3. 튕겨나가는 물리 (Impulse Resolution)
            // 상대 속도 계산
            double rvx = b.getVx() - a.getVx();
            double rvy = b.getVy() - a.getVy();

            // 법선 방향의 상대 속도 (내적)
            double velAlongNormal = rvx * nx + rvy * ny;

            // 이미 멀어지고 있다면 계산하지 않음 (무한 충돌 방지)
            if (velAlongNormal > 0) return;

            // 반발 계수 (두 객체 중 낮은 값을 사용하거나 평균 사용)
            double e = Math.min(a.getConfig().getRestitution(), b.getConfig().getRestitution());

            // 충격량(j) 계산 공식
            double j = -(1 + e) * velAlongNormal;
            j /= (1 / massA + 1 / massB);

            // 충격량 적용
            double impulseX = j * nx;
            double impulseY = j * ny;

            a.addVx(- (1 / massA) * impulseX);
            a.addVy(- (1 / massA) * impulseY);
            b.addVx(+ (1 / massB) * impulseX);
            b.addVy(+ (1 / massB) * impulseY);
        }
    }

    public final int getMouseX() { return ViewMetrics.getVirtualMouseX(); }
    public final int getMouseY() { return ViewMetrics.getVirtualMouseY(); }
    public final double getScaleX() { return ViewMetrics.getScaleX(); }
    public final double getScaleY() { return ViewMetrics.getScaleY(); }
    public final int getWindowHeight() { return ViewMetrics.getWindowHeight(); }
    public final int getWindowWidth() { return ViewMetrics.getWindowWidth(); }

    public void setHitBoxRender(boolean b) {hitBoxRender = b;}
    public void setPause(boolean b) {pause = b;}

    public Camera getCamera() {return camera;}
}
