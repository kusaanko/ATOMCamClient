package com.github.kusaanko.atomcamclient;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

public class UpdateNotifier extends JFrame {
    public UpdateNotifier(JSONArray json) {
        super();
        this.setLayout(new BorderLayout());
        this.add(new JLabel("新しいアップデートが利用可能です!"), BorderLayout.NORTH);
        StringBuilder history = new StringBuilder("<html><body>");
        for (int i = 0; i < json.length(); i++) {
            if (i > 20) break;
            JSONObject object = json.getJSONObject(i);
            history.append("<h1>").append(object.getString("name")).append("</h1>");
            history.append("<p>").append(object.getString("body").replace("\n", "<br>")).append("</p>");
            history.append("<p></p>");
        }
        history.append("</body></html>");
        JEditorPane updateHistory = new JEditorPane("text/html", history.toString());
        updateHistory.setEditable(false);
        JButton laterButton = new JButton("後で");
        laterButton.addActionListener(e -> {
            this.dispose();
        });
        JButton updateButton = new JButton("今すぐ更新する");
        updateButton.addActionListener(e -> {
            new Updater(json.getJSONObject(0));
            this.dispose();
        });
        JPanel buttonsPane = new JPanel(new GridLayout(1, 2));
        buttonsPane.add(laterButton);
        buttonsPane.add(updateButton);
        this.add(buttonsPane, BorderLayout.SOUTH);
        this.add(new JScrollPane(updateHistory), BorderLayout.CENTER);
        this.setSize(600, 500);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("ATOMCamClient - " + Main.version);
        this.setVisible(true);
    }
}
