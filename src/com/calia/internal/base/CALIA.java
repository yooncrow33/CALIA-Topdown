package com.calia.internal.base;

import com.calia.internal.editer.CaliaConfig;
import javax.swing.JOptionPane;
import java.io.*;
import java.util.Properties;

public class CALIA {
    // 엔진 전역에서 사용할 불변 상수
    public static final String CONFIG_NAME;
    public static final int WIDTH, HEIGHT;
    public static final int WORLD_WIDTH, WORLD_HEIGHT;
    public static final int PHYSICS;
    public static final String FPS;
    public static final boolean CONSOLE, ANTI_ALIASING;

    static {
        File buildFile = new File("buildConfig.calia");
        String targetConfigName = "Basic";

        // 1. buildConfig.calia 읽기 시도
        if (!buildFile.exists()) {
            // [핵심] 파일이 없으면 여기서 직접 생성한다.
            createDefaultBuildConfig(buildFile);
            //JOptionPane.showMessageDialog(null,
              //      "설정 파일(buildConfig.calia)이 없어 'Basic'으로 자동 생성합니다.",
                //    "Config Auto-Created", JOptionPane.INFORMATION_MESSAGE);
        } else {
            try (FileInputStream in = new FileInputStream(buildFile)) {
                Properties p = new Properties();
                p.load(in);
                targetConfigName = p.getProperty("configName", "Basic");
            } catch (IOException e) {
                targetConfigName = "Basic"; // 읽기 실패 시 강제 Basic
            }
        }

        // 2. 실제 상세 설정 로드 (user.home/calia/{name}/config.calia)
        String userHome = System.getProperty("user.home");
        File configPath = new File(userHome + File.separator + "calia" + File.separator + targetConfigName + File.separator + "config.calia");

        CaliaConfig loader = new CaliaConfig(targetConfigName);

        if (configPath.exists()) {
            try (FileInputStream in = new FileInputStream(configPath)) {
                Properties p = new Properties();
                p.load(in);
                loader.fromProperties(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 상세 설정 파일도 없으면 에디터에서 만든 Basic 설정을 찾아보고, 그것도 없으면 기본값 적용
            JOptionPane.showMessageDialog(null,
                    targetConfigName + " 상세 설정을 찾을 수 없습니다. 기본값을 로드합니다.",
                    "Detail Config Missing", JOptionPane.ERROR_MESSAGE);
        }

        // 3. 상수에 최종 할당 (여기서 상수로 박아버림)
        CONFIG_NAME = targetConfigName;
        WIDTH = Integer.parseInt(loader.width);
        HEIGHT = Integer.parseInt(loader.height);
        WORLD_WIDTH = Integer.parseInt(loader.worldWidth);
        WORLD_HEIGHT = Integer.parseInt(loader.worldHeight);
        PHYSICS = loader.physics;
        FPS = loader.fps;
        CONSOLE = loader.console;
        ANTI_ALIASING = loader.antiAliasing;
    }

    // 파일 없을 때 Basic으로 만드는 메서드
    private static void createDefaultBuildConfig(File file) {
        Properties props = new Properties();
        props.setProperty("configName", "Basic");
        try (FileOutputStream out = new FileOutputStream(file)) {
            props.store(out, "CALIA Auto-Generated Build Config");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}