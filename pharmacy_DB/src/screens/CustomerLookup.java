package screens;

import main.Pharmacy_DB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

public class CustomerLookup extends JPanel {

    private JLabel labelID = new JLabel("ID: ");
    private JTextField textID = new JTextField(10);
    private JLabel labelName = new JLabel("Name: ");
    private JTextField textName = new JTextField(10);
    private JButton buttonSearch = new JButton("Search");
    private JButton buttonBack = new JButton("Back");

    private JPanel messageContainer = new JPanel(new GridLayout(1, 1));
    private JLabel searchMessage = new JLabel("Searching...");

    private GridBagConstraints constraints = new GridBagConstraints();

    DefaultTableModel model = new DefaultTableModel();
    JTable table = new JTable(model);

    private JPanel left = new JPanel(new GridBagLayout());;
    private JPanel right = new JPanel(new BorderLayout());;

    public CustomerLookup() {

        // important! call JPanel constructor and pass GridBagLayout
        super(new GridBagLayout());

        // set contraints and padding
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.weightx = 0;

        constraints.gridx = 0;
        add(left, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.weightx = 1;
        add(right, constraints);
        constraints.gridheight = 1;

        constraints.fill = GridBagConstraints.NONE;

        // add components to the panel
        constraints.gridx = 0;
        constraints.gridy = 0;
        left.add(labelID, constraints);

        constraints.gridx = 1;
        left.add(textID, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        left.add(labelName, constraints);

        constraints.gridx = 1;
        left.add(textName, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        left.add(buttonSearch, constraints);

        constraints.gridy = 3;
        left.add(buttonBack, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        messageContainer.add(searchMessage);
        left.add(messageContainer, constraints);
        searchMessage.setVisible(false);

        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Phone");
        model.addColumn("Policy ID");

        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);

        table.setFillsViewportHeight(true);
        JScrollPane tableContainer = new JScrollPane(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        right.add(tableContainer, BorderLayout.CENTER);

        // set border for the panel
        left.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Search"));

        // set border for the panel
        right.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Customers"));

        buttonSearch.addActionListener(new SearchButton());
        buttonBack.addActionListener(new BackButton());
    }

    private void fillTable(DefaultTableModel model, ResultSet rs) {
        model.setRowCount(0);
        if (rs != null) {
            try {
                while (rs.next()) {
                    int id = rs.getInt("customer_id");
                    String name = rs.getString("name");
                    String phone = rs.getString("phone_number");
                    int policy = rs.getInt("insurance_policy_id");
                    model.addRow(new Object[]{String.format("%08d", id), name, phone, String.format("%08d", policy)});
                }
            } catch (SQLException e) {
                // stop
            }
        }
    }

    private class SearchButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            searchMessage.setText("Searching...");
            searchMessage.setVisible(true);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    StringBuilder query = new StringBuilder();
                    StringBuilder message = new StringBuilder();

                    query.append("SELECT * FROM Customer WHERE LOWER(name) LIKE LOWER('%");
                    query.append(textName.getText());
                    query.append("%')");

                    String id = textID.getText();

                    if (id.length() != 0) {
                        query.append(" AND customer_id = ");
                        query.append(id);
                    }

                    query.append(" ORDER BY customer_id");

                    fillTable(model, Pharmacy_DB.getResults(query.toString()));

                    message.append(model.getRowCount());
                    message.append(" results found.");

                    searchMessage.setText(message.toString());
                    revalidate();
                    repaint();
                }
            });
        }
    }

    private class BackButton implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Pharmacy_DB.switchScreen(Pharmacy_DB.getHomePanel());
        }
    }

}