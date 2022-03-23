package com.github.kusaanko.atomcamclient;

import com.github.kusaanko.atomcamclient.api.ATOMApi;
import com.github.kusaanko.atomcamclient.api.av.audio.AudioData;
import com.github.kusaanko.atomcamclient.api.av.video.FrameData;
import com.github.kusaanko.atomcamclient.api.device.ATOMCamera;
import com.github.kusaanko.atomcamclient.api.device.ATOMDevice;
import com.github.kusaanko.atomcamclient.opengl.ImageRenderer;
import org.lwjgl.opengl.awt.GLData;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class CameraPanel extends JPanel {
    private ImageRenderer imageRenderer;
    private ATOMCamera atomCamera;
    private boolean stopGetVideoThread = true;
    private FrameData latestImage;
    private final Runnable getVideoRunnable = () -> {
        while (!stopGetVideoThread) {
            FrameData frameData = atomCamera.getFrame();
            if (frameData != null) {
                imageRenderer.putImage(frameData.getData(), frameData.getWidth(), frameData.getHeight());
                latestImage = frameData;
            }
        }
    };
    private JPanel statusPanel;
    private boolean isAddedStatusPanel;
    private JLabel cameraNameLabel;
    private Popup audioPopup;
    private JavaAudioPlayer audioPlayer;
    private final Thread audioThread = new Thread() {
        @Override
        public void run() {
            while (!stopGetVideoThread) {
                AudioData audioData = atomCamera.getAudio();
                if (audioPlayer != null && audioData != null) {
                    audioPlayer.queue(audioData);
                }
            }
        }
    };

    public CameraPanel(JFrame parent) {
        super(new BorderLayout());
        GLData glData = new GLData();
        glData.majorVersion = 3;
        glData.minorVersion = 2;
        glData.profile = GLData.Profile.CORE;
        glData.forwardCompatible = true;
        imageRenderer = new ImageRenderer(glData);
        this.add(this.imageRenderer, BorderLayout.CENTER);
        JPopupMenu cameraMenu = new JPopupMenu();
        {
            JMenuItem chooseCamera = new JMenuItem("カメラを選択");
            cameraMenu.add(chooseCamera);
            chooseCamera.addActionListener(e -> {
                new Thread(() -> {
                    stopGetVideoThread = true;
                    if (this.atomCamera != null) {
                        this.atomCamera.stop();
                        this.atomCamera.close();
                        this.atomCamera = null;
                    }
                    List<ATOMDevice> devices = ATOMApi.getDeviceList().getDevices();
                    CameraChooser cameraChooser = new CameraChooser(parent, devices);
                    cameraChooser.setVisible(true);
                    if (cameraChooser.getSelectedDevice() != null) {
                        this.cameraNameLabel.setText(cameraChooser.getSelectedDevice().getNickname());
                        this.atomCamera = new ATOMCamera(cameraChooser.getSelectedDevice());
                        if (!this.atomCamera.createConnection()) {
                            JOptionPane.showMessageDialog(parent, "接続に失敗しました。やり直してください。");
                            return;
                        }
                        this.atomCamera.start();
                        while (!this.atomCamera.isVerified()) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                        this.atomCamera.enableVideo();
                        stopGetVideoThread = false;
                        new Thread(getVideoRunnable).start();
                        new Thread(() -> {
                            AudioData audioData = atomCamera.getAudio();
                            if (audioData == null) {
                                JOptionPane.showMessageDialog(parent, "認証に失敗しました。やり直してください。");
                                this.close();
                            }
                            if (audioPlayer == null && audioData != null) {
                                audioPlayer = new JavaAudioPlayer(audioData);
                                audioPlayer.initialize(audioData);
                                audioThread.start();
                                audioPlayer.play();
                            }
                        }).start();
                    }
                }).start();
            });
            JMenuItem switchStatusItem = new JMenuItem("ステータスの表示を切り替え");
            switchStatusItem.addActionListener(e -> {
                if (this.isAddedStatusPanel) {
                    this.remove(this.statusPanel);
                } else {
                    this.add(this.statusPanel, BorderLayout.SOUTH);
                }
                this.revalidate();
                this.repaint();
                this.isAddedStatusPanel = !this.isAddedStatusPanel;
            });
            cameraMenu.add(switchStatusItem);
            JMenuItem copyImageItem = new JMenuItem("現在のフレームをコピー");
            copyImageItem.addActionListener(e -> {
                new Thread(() -> {
                    Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                    c.setContents(new Transferable() {
                        @Override
                        public DataFlavor[] getTransferDataFlavors() {
                            return new DataFlavor[]{DataFlavor.imageFlavor};
                        }

                        @Override
                        public boolean isDataFlavorSupported(DataFlavor flavor) {
                            DataFlavor[] flavors = getTransferDataFlavors();
                            for (DataFlavor dataFlavor : flavors) {
                                if (flavor.equals(dataFlavor)) {
                                    return true;
                                }
                            }
                            return false;
                        }

                        @Override
                        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                            if (flavor.equals(DataFlavor.imageFlavor)) {
                                BufferedImage image = new BufferedImage(latestImage.getWidth(), latestImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                                int[] rgb = new int[latestImage.getWidth() * latestImage.getHeight()];
                                byte[] d = latestImage.getData();
                                for (int y = 0; y < latestImage.getHeight(); y++) {
                                    for (int x = 0; x < latestImage.getWidth(); x++) {
                                        int pos = y * latestImage.getWidth() + x;
                                        rgb[pos] = (((d[pos * 3 + 2] & 0xFF) << 16) + ((d[pos * 3 + 1] & 0xFF) << 8) + (d[pos * 3] & 0xFF)) + (0xFF << 24);
                                    }
                                }
                                image.setRGB(0, 0, latestImage.getWidth(), latestImage.getHeight(), rgb, 0, latestImage.getWidth());
                                return image;
                            } else {
                                throw new UnsupportedFlavorException(flavor);
                            }
                        }
                    }, (clipboard, contents) -> {
                    });
                }).start();
            });
            cameraMenu.add(copyImageItem);
        }
        cameraMenu.setLightWeightPopupEnabled(true);
        this.imageRenderer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON3) {
                    cameraMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        this.statusPanel = new JPanel(new GridLayout(1, 4));
        this.cameraNameLabel = new JLabel();
        this.statusPanel.add(this.cameraNameLabel);
        this.add(this.statusPanel, BorderLayout.SOUTH);
        this.isAddedStatusPanel = true;
        JButton audioSwitch = new JButton();
        {
            try {
                BufferedImage bufferedImage = ImageIO.read(this.getClass().getResource("/img/speaker-filled-audio-tool.png"));
                audioSwitch.setIcon(new ImageIcon(Util.resizeImage(bufferedImage, 20, 20)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.statusPanel.add(audioSwitch);
        GridBagLayout layout = new GridBagLayout();
        JPanel audioControl = new JPanel();
        audioControl.setLayout(layout);
        audioControl.setSize(200, 300);
        audioControl.setPreferredSize(new Dimension(200, 300));
        JSlider audioSlider = new JSlider();
        audioSlider.setMinimum(0);
        audioSlider.setMaximum(500);
        audioSlider.setValue(100);
        audioSlider.setOrientation(SwingConstants.VERTICAL);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 4;
        layout.setConstraints(audioSlider, gridBagConstraints);
        JLabel volumeLabel = new JLabel("音量: 100");
        audioSlider.addChangeListener(e -> {
            volumeLabel.setText("音量: " + audioSlider.getValue());
            if (this.audioPlayer != null) {
                this.audioPlayer.setVolume(audioSlider.getValue() / 100f * 10);
            }
        });
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 4;
        layout.setConstraints(volumeLabel, gridBagConstraints);
        JCheckBox speakerCheckBox = new JCheckBox();
        speakerCheckBox.setSelected(true);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 1;
        layout.setConstraints(speakerCheckBox, gridBagConstraints);
        audioControl.add(audioSlider);
        audioControl.add(volumeLabel);
        audioControl.add(speakerCheckBox);
        audioSwitch.addActionListener(e -> {
            if (audioPopup != null) {
                audioPopup.hide();
                audioPopup = null;
            } else {
                PopupFactory pf = PopupFactory.getSharedInstance();
                Point p = audioSwitch.getLocationOnScreen();
                audioPopup = pf.getPopup(audioSwitch, audioControl, p.x + ((audioSwitch.getWidth() - audioControl.getWidth()) / 2), p.y - audioControl.getHeight());
                audioPopup.show();
            }
        });
    }

    public void start() {
        this.imageRenderer.start();
    }

    public void close() {
        this.stopGetVideoThread = true;
        if (this.atomCamera != null) {
            this.atomCamera.close();
        }
        this.atomCamera = null;
    }
}
