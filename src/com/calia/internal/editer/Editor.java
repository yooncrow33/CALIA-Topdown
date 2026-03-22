package com.calia.internal.editer;

import com.calia.internal.base.CALIA;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class Editor {
    private JFrame frame;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // 데이터 관리
    private boolean isFirstRun = true;
    private DefaultListModel<String> listModel;
    private JList<String> configList;
    private JTextArea infoArea;
    private String selectedConfig = null;

    // 설정 입력 필드들
    private JTextField tfName, tfWidth, tfHeight;
    private JTextField rfName, rfWidth, rfHeight;
    private JSlider sliderPhysics; // 물리 과장 바
    private JComboBox<String> comboFPS;
    private JCheckBox cbConsole, cbAntiAliasing;
    private boolean isEditMode = false;

    private ArrayList<CaliaConfig> configListObj = new ArrayList<>();
    private final String BASE_DIR = System.getProperty("user.home") + File.separator + "calia";

    public Editor() {
        frame = new JFrame("CALIA Engine Editor");
        frame.setSize(900, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // UI 생성 전에 데이터부터 먼저 세팅
        listModel = new DefaultListModel<>();
        loadAllConfigs();
        isFirstRun = configListObj.isEmpty();

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // 이제 createMainPanel이 실행될 때 로드된 listModel을 정상적으로 참조함
        cardPanel.add(createWelcomePanel(), "Welcome");
        cardPanel.add(createMainPanel(), "Main");
        cardPanel.add(createConfigPanel(), "CreateConfig");

        frame.add(cardPanel);

        if (isFirstRun) cardLayout.show(cardPanel, "Welcome");
        else cardLayout.show(cardPanel, "Main");

        frame.setVisible(true);
    }

    // [화면 1] 환영 화면
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel("<html><center><h2>CALIA Engine</h2><br>환영합니다!<br>프로젝트를 관리하려면 시작하기를 누르세요.</center></html>", SwingConstants.CENTER);

        JButton btnStart = new JButton("시작하기");
        btnStart.setPreferredSize(new Dimension(0, 60));
        btnStart.addActionListener(e -> {
            isFirstRun = false;
            cardLayout.show(cardPanel, "Main");
        });

        panel.add(lbl, BorderLayout.CENTER);
        panel.add(btnStart, BorderLayout.SOUTH);
        return panel;
    }

    // [화면 2] 메인 화면 (목록 + 정보창 + 삭제 경고 포함)
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 기존의 listModel = new DefaultListModel<>(); 및 더미 데이터 추가 부분 삭제
        configList = new JList<>(listModel); // 생성자에서 미리 로드된 listModel을 그대로 사용
        JScrollPane scrollPane = new JScrollPane(configList);
        scrollPane.setPreferredSize(new Dimension(250, 0));
        scrollPane.setBorder(new TitledBorder("내 프로젝트"));

        infoArea = new JTextArea("\n 프로젝트를 선택하면 상세 정보가 표시됩니다.");
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        infoArea.setBackground(new Color(240, 240, 240));
        infoArea.setBorder(BorderFactory.createTitledBorder("상세 설정 정보"));

        configList.addListSelectionListener(e -> {
            selectedConfig = configList.getSelectedValue();
            if (selectedConfig != null) updatePreviewInfo(selectedConfig);
        });

        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        JButton btnNew = new JButton("새로 만들기");
        JButton btnEdit = new JButton("설정 수정");
        JButton btnDelete = new JButton("삭제");
        JButton btnRun = new JButton("엔진 실행");

        btnNew.addActionListener(e -> {
            isEditMode = false;
            resetFields();
            cardLayout.show(cardPanel, "CreateConfig");
        });

        btnEdit.addActionListener(e -> {
            if (selectedConfig == null) {
                JOptionPane.showMessageDialog(frame, "수정할 프로젝트를 선택하세요.");
                return;
            }
            isEditMode = true;
            loadDataToFields(selectedConfig);
            cardLayout.show(cardPanel, "CreateConfig");
        });

        // 삭제 버튼: 경고 메시지 복구
        btnDelete.addActionListener(e -> {
            if (selectedConfig == null) {
                JOptionPane.showMessageDialog(frame, "삭제할 항목을 선택하세요.");
                return;
            }
            int result = JOptionPane.showConfirmDialog(frame,
                    selectedConfig + " 설정을 정말 삭제하시겠습니까?",
                    "삭제 확인", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                // 객체 리스트와 UI 리스트에서 제거
                configListObj.removeIf(config -> config.name.equals(selectedConfig));
                listModel.removeElement(selectedConfig);

                // 폴더 삭제 로직 (내부 파일까지 삭제해야 함)
                File configDir = new File(BASE_DIR, selectedConfig);
                if(configDir.exists()) {
                    new File(configDir, "config.calia").delete();
                    configDir.delete();
                }

                // 삭제된 상태를 editorData.calia에 반영하기 위해 전체 재저장
                saveAllConfigs();

                infoArea.setText("\n 프로젝트가 성공적으로 삭제되었습니다.");
                selectedConfig = null;
            }
        });

        btnPanel.add(btnNew); btnPanel.add(btnEdit); btnPanel.add(btnDelete); btnPanel.add(btnRun);
        panel.add(scrollPane, BorderLayout.WEST);
        panel.add(infoArea, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // [화면 3] 설정 편집 화면 (물리 슬라이더 바 눈금 복구)
    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(null);
        int lx = 280, fx = 420;

        JLabel title = new JLabel("프로젝트 설정 편집", SwingConstants.CENTER);
        title.setBounds(0, 30, 900, 40);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        tfName = new JTextField(); tfName.setBounds(fx, 100, 200, 30);
        addLabel(panel, "프로젝트 이름:", lx, 100);

        tfWidth = new JTextField("1920"); tfWidth.setBounds(fx, 150, 95, 30);
        tfHeight = new JTextField("1080"); tfHeight.setBounds(fx + 105, 150, 95, 30);
        addLabel(panel, "해상도(W/H):", lx, 150);

        rfWidth = new JTextField("1920"); rfWidth.setBounds(fx, 200, 95, 30);
        rfHeight = new JTextField("1080"); rfHeight.setBounds(fx + 105, 200, 95, 30);
        addLabel(panel, "월드 크기(W/H):", lx, 200);

        // 물리 과장 슬라이더 복구 (눈금 및 라벨 포함)
        sliderPhysics = new JSlider(0, 100, 50);
        sliderPhysics.setBounds(fx, 250, 220, 50);
        sliderPhysics.setMajorTickSpacing(50);
        sliderPhysics.setMinorTickSpacing(10);
        sliderPhysics.setPaintTicks(true); // 눈금 표시
        sliderPhysics.setPaintLabels(true); // 0, 50, 100 라벨 표시
        addLabel(panel, "물리 과장 (0-1):", lx, 250);

        comboFPS = new JComboBox<>(new String[]{"30 FPS", "60 FPS"});
        comboFPS.setBounds(fx, 320, 200, 30);
        addLabel(panel, "타겟 프레임:", lx, 320);

        cbConsole = new JCheckBox("콘솔 활성화"); cbConsole.setBounds(fx, 370, 200, 30);
        cbAntiAliasing = new JCheckBox("안티에일리언싱"); cbAntiAliasing.setBounds(fx, 400, 200, 30);

        JButton btnSave = new JButton("저장");
        btnSave.setBounds(510, 480, 100, 40);
        btnSave.addActionListener(e -> {
            String name = tfName.getText().trim();
            if(!name.isEmpty()) {
                CaliaConfig targetConfig = null;

                // 수정 모드인 경우 기존 객체 찾기
                if (isEditMode) {
                    for (CaliaConfig config : configListObj) {
                        if (config.name.equals(name)) {
                            targetConfig = config;
                            break;
                        }
                    }
                }
                // 신규 모드인 경우 객체 생성 및 리스트 추가
                if (targetConfig == null) {
                    targetConfig = new CaliaConfig(name);
                    configListObj.add(targetConfig);
                    listModel.addElement(name);
                }

                // 입력된 UI 값을 객체에 반영
                targetConfig.width = tfWidth.getText();
                targetConfig.height = tfHeight.getText();
                targetConfig.worldWidth = rfWidth.getText();
                targetConfig.worldHeight = rfHeight.getText();
                targetConfig.physics = sliderPhysics.getValue();
                targetConfig.fps = (String) comboFPS.getSelectedItem();
                targetConfig.console = cbConsole.isSelected();
                targetConfig.antiAliasing = cbAntiAliasing.isSelected();

                // 실제 파일로 저장
                saveAllConfigs();

                cardLayout.show(cardPanel, "Main");

                // [수정된 부분] 메인 화면 복귀 직후, 현재 선택된 항목의 프리뷰를 즉시 다시 렌더링
                if (name.equals(selectedConfig)) {
                    updatePreviewInfo(name);
                }
            }
        });

        JButton btnBack = new JButton("취소");
        btnBack.setBounds(340, 480, 100, 40);
        btnBack.addActionListener(e -> cardLayout.show(cardPanel, "Main"));

        panel.add(title); panel.add(tfName); panel.add(tfWidth); panel.add(tfHeight); panel.add(rfWidth); panel.add(rfHeight);
        panel.add(sliderPhysics); panel.add(comboFPS);
        panel.add(cbConsole); panel.add(cbAntiAliasing); panel.add(btnSave); panel.add(btnBack);

        return panel;
    }

    private void addLabel(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text); l.setBounds(x, y, 120, 30); p.add(l);
    }

    private void updatePreviewInfo(String name) {
        for (CaliaConfig config : configListObj) {
            if (config.name.equals(name)) {
                infoArea.setText("\n [ Project: " + config.name + " ]\n" +
                        " ------------------------------------\n" +
                        " Resolution: " + config.width + " x " + config.height + "\n" +
                        " World Size: " + config.worldWidth + " x " + config.worldHeight + "\n" +
                        " Physics Multiplier: " + config.physics + "\n" +
                        " Target Frame: " + config.fps + "\n" +
                        " Console: " + (config.console ? "Enabled" : "Disabled") +
                        " / AA: " + (config.antiAliasing ? "Enabled" : "Disabled") + "\n" +
                        " ------------------------------------");
                break;
            }
        }
    }

    private void resetFields() {
        tfName.setText(""); tfName.setEditable(true);
        tfWidth.setText("1920"); tfHeight.setText("1080");
        rfWidth.setText("19200"); rfHeight.setText("10800");
        sliderPhysics.setValue(50); comboFPS.setSelectedIndex(1);
        cbConsole.setSelected(false); cbAntiAliasing.setSelected(false);
    }

    private void loadDataToFields(String name) {
        tfName.setText(name);
        tfName.setEditable(false);

        // 객체 리스트에서 이름이 일치하는 설정을 찾아 UI에 세팅
        for (CaliaConfig config : configListObj) {
            if (config.name.equals(name)) {
                tfWidth.setText(config.width);
                tfHeight.setText(config.height);
                rfWidth.setText(config.worldWidth);
                rfHeight.setText(config.worldHeight);
                sliderPhysics.setValue(config.physics);
                comboFPS.setSelectedItem(config.fps);
                cbConsole.setSelected(config.console);
                cbAntiAliasing.setSelected(config.antiAliasing);
                break;
            }
        }
    }

    private void saveAllConfigs() {
        File baseDir = new File(BASE_DIR);
        if (!baseDir.exists()) baseDir.mkdirs();

        // 1-1. editorData.calia 저장
        Properties editorProps = new Properties();
        editorProps.setProperty("length", String.valueOf(configListObj.size()));
        for (int i = 0; i < configListObj.size(); i++) {
            editorProps.setProperty("config_" + i, configListObj.get(i).name);
        }

        try (FileOutputStream fos = new FileOutputStream(new File(baseDir, "editorData.calia"))) {
            editorProps.store(fos, "CALIA Editor Data");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 1-2. 개별 설정 폴더 및 config.calia 저장
        for (CaliaConfig config : configListObj) {
            File configDir = new File(baseDir, config.name);
            if (!configDir.exists()) configDir.mkdirs();

            try (FileOutputStream fos = new FileOutputStream(new File(configDir, "config.calia"))) {
                config.toProperties().store(fos, "CALIA Project Config: " + config.name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 2. 초기 기동 시 데이터 로드
    // 2. 초기 기동 시 데이터 로드 및 Basic 기본 설정 강제 생성
    private void loadAllConfigs() {
        File baseDir = new File(BASE_DIR);
        File editorDataFile = new File(baseDir, "editorData.calia");

        configListObj.clear();
        listModel.clear();

        // 기존 데이터 로드 로직
        if (editorDataFile.exists()) {
            Properties editorProps = new Properties();
            try (FileInputStream fis = new FileInputStream(editorDataFile)) {
                editorProps.load(fis);
                int length = Integer.parseInt(editorProps.getProperty("length", "0"));

                for (int i = 0; i < length; i++) {
                    String configName = editorProps.getProperty("config_" + i);
                    if (configName == null) continue;

                    File configDir = new File(baseDir, configName);
                    File configFile = new File(configDir, "config.calia");

                    if (configFile.exists()) {
                        Properties configProps = new Properties();
                        try (FileInputStream cfis = new FileInputStream(configFile)) {
                            configProps.load(cfis);
                            CaliaConfig config = new CaliaConfig(configName);
                            config.fromProperties(configProps);

                            configListObj.add(config);
                            listModel.addElement(configName); // UI 리스트 업데이트
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Basic 설정 누락 검사 및 생성 로직
        boolean hasBasic = false;
        for (CaliaConfig config : configListObj) {
            if (config.name.equals("Basic")) {
                hasBasic = true;
                break;
            }
        }

        // Basic이 없으면 디폴트 객체를 생성하고 저장
        if (!hasBasic) {
            CaliaConfig basicConfig = new CaliaConfig("Basic");
            basicConfig.width = "1920";
            basicConfig.height = "1080";
            basicConfig.worldWidth = "19200";
            basicConfig.worldHeight = "10800";
            basicConfig.physics = 50;
            basicConfig.fps = "60 FPS";
            basicConfig.console = false;
            basicConfig.antiAliasing = true;

            configListObj.add(basicConfig);
            listModel.addElement("Basic");

            // 실제 파일(editorData.calia 및 Basic/config.calia) 생성 강제 실행
            saveAllConfigs();

            int i = CALIA.PHYSICS;
        }
    }

    public static void main(String[] args) {
        System.out.println("엔진 설정 로딩 시작...");
        System.out.println("설정 이름: " + CALIA.CONFIG_NAME);
        System.out.println("엔진 설정 로딩 완료.");
        SwingUtilities.invokeLater(Editor::new);
    }
}