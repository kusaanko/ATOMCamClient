package com.github.kusaanko.atomcamclient;

import com.github.kusaanko.atomcamclient.api.device.ATOMDevice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class CameraChooser extends JDialog {
    private DefaultListModel<ATOMDevice> listModel;
    private JList<ATOMDevice> jList;
    private Consumer<ATOMDevice> callback;
    private ATOMDevice selectedDevice;

    public CameraChooser(JFrame parent, List<ATOMDevice> cameraList) {
        super(parent);
        this.setLayout(new BorderLayout());
        listModel = new DefaultListModel<>();
        for (ATOMDevice device : cameraList) {
            listModel.addElement(device);
        }
        jList = new JList<>(listModel);
        jList.setCellRenderer(new ListCellRenderer<ATOMDevice>() {
            JLabel label;

            @Override
            public Component getListCellRendererComponent(JList<? extends ATOMDevice> list, ATOMDevice device, int index, boolean isSelected, boolean cellHasFocus) {
                if (label == null) {
                    label = new JLabel();
                    label.setOpaque(true);
                    label.setFont(label.getFont().deriveFont(18f));
                    label.setPreferredSize(new Dimension(10, 50));
                }
                label.setText(device.getNickname());
                // TODO: very slow
                /*if(device.getProductModelLogoUrl() != null) {
                    try {
                        URL url = new URL(device.getProductModelLogoUrl());
                        ImageIcon icon = new ImageIcon(url);
                        label.setIcon(icon);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }else {
                    label.setIcon(null);
                }*/
                if (isSelected) {
                    label.setBackground(new Color(0, 150, 240));
                }
                return label;
            }
        });
        JPanel buttonPane = new JPanel(new GridLayout(1, 3));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("キャンセル");
        buttonPane.add(new JPanel());
        buttonPane.add(cancelButton);
        buttonPane.add(okButton);
        okButton.addActionListener(e -> {
            this.callback();
        });
        jList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    CameraChooser.this.callback();
                }
            }
        });
        this.add(buttonPane, BorderLayout.SOUTH);
        JScrollPane jScrollPane = new JScrollPane(jList);
        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.setSize(500, 600);
        this.setTitle("カメラを選択");
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setModal(true);
    }

    public void setCallback(Consumer<ATOMDevice> callback) {
        this.callback = callback;
    }

    private void callback() {
        if (this.jList.getSelectedValue() != null) {
            if (this.callback != null) {
                this.callback.accept(this.jList.getSelectedValue());
            }
            this.selectedDevice = this.jList.getSelectedValue();
            this.dispose();
        }
    }

    public ATOMDevice getSelectedDevice() {
        return this.selectedDevice;
    }
}
