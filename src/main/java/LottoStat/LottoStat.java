package LottoStat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.poi.xssf.usermodel.*;

public class LottoStat {
    public static SheetHandler handler;
    public static JTable resultTable;
    public static TableModel resultTableModel;
    public static ArrayList<String> columns;
    public static JFrame mainFrame;
    public static int languageInt; // 0 = finnish, 1 = english

    public static String[] inputText = {"Syötä tulos pilkuilla erotettuna, esim. 1,2,30,4,531,6", "Insert result separated by commas, e.g. 1,2,30,4,531,6"};
    public static String[] sendText = {"Lähetä", "Send"};
    public static String[] langText = {"Vaihda kieltä", "Change language"};
    public static String[] wrongFormatText = {"Väärä muoto, tarkista syöte. Vain numerot (1,2,3,4...) ja pilkut (,) sallittuja", "Wrong format, check your input. Only numbers( 1,2,3,4...) and commas (,) allowed"};
    public static String[] addSuccessText = {"Lisätty", "Added"};
    public static String[] eraseButtonText = {"Poista kaikki", "Erase all"};
    public static String[] deleteSuccessText = {"Poistettu", "Deleted"};


    public static void main(String[] args) throws URISyntaxException, IOException {
        try {
            File stats = new File("stats.xlsx");

            InputStream fis = new BufferedInputStream(new FileInputStream(stats));
            XSSFWorkbook excelHandler = new XSSFWorkbook(fis);

            SheetHandler sheetHandler = new SheetHandler(excelHandler);

            handler = sheetHandler;
            languageInt = 0;

            swing();
        } catch (FileNotFoundException e1) {
            showStatusMsg("Virhe/Error " +  e1.getClass(), true);
        } catch (IOException e1) {
            showStatusMsg("Virhe/Error " +  e1.getClass(), true);
        } catch (Error e1) {
            showStatusMsg("Virhe/Error " +  e1.getClass(), true);
        }
    }

    public static void swing() {
        mainFrame = new JFrame("LottoStat");

        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setSize(1300, 600);
        mainFrame.setLayout(null);
        mainFrame.setVisible(true);
        mainFrame.setResizable(false);

        JTextArea insert = new JTextArea();
        JLabel insertTitle = new JLabel(inputText[languageInt]);

        // button
        JButton submitButton = new JButton(sendText[languageInt]);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String insertValue = insert.getText();          
                
                if(Pattern.matches("^[0-9,]*$", insertValue)) {
                    for(String value : insertValue.split(",")) {
                        try {
                            handler.incrementOrCreate(Integer.valueOf(value));
                            
                            resultTable.setModel(new DefaultTableModel(handler.getAllRows(), columns.toArray()));
                            showStatusMsg(addSuccessText[languageInt], false);

                        }
                        catch (NumberFormatException e1) {
                            showStatusMsg(wrongFormatText[languageInt], true);
                        } catch (IOException e1) {
                            showStatusMsg("Virhe/Error " +  e1.getClass(), true);
                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else showStatusMsg(wrongFormatText[languageInt], true);
            }
        });
        
        JButton eraseDataButton = new JButton(eraseButtonText[languageInt]);
        eraseDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    handler.eraseData();
                    resultTable.setModel(new DefaultTableModel(handler.getAllRows(), columns.toArray()));
                    showStatusMsg(deleteSuccessText[languageInt], false);
                } catch (IOException e1) {
                    showStatusMsg("Virhe/Error " +  e1.getClass(), true);
                } catch (URISyntaxException e1) {
                    showStatusMsg("Virhe/Error " +  e1.getClass(), true);
                    e1.printStackTrace();
                }
            }
        });
        JButton langButton = new JButton(langText[languageInt]);
        langButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                languageInt = languageInt == 1 ? 0 : 1;

                insertTitle.setText(inputText[languageInt]);
                submitButton.setText(sendText[languageInt]);
                langButton.setText(langText[languageInt]); 
                eraseDataButton.setText(eraseButtonText[languageInt]); 
            }
        });


        // result table
        columns = new ArrayList<String>();
        columns.add("Num");
        columns.add("x");
        resultTableModel = new DefaultTableModel(handler.getAllRows(), columns.toArray());
 
        resultTable = new JTable(resultTableModel) {
            public boolean isCellEditable(int row, int column) {                
                    return false;               
            };
        };
        JScrollPane scrollPane = new JScrollPane(resultTable);

        JLabel credits = new JLabel("Mikko Egor Legezin, 2024");

        // bounds
        insert.setBounds(10, 50, 1000, 30);
        insertTitle.setBounds(10, 20, 1000, 30);
        submitButton.setBounds(10, 90, 100, 30);
        scrollPane.setBounds(10, 130, 400, 300);
        langButton.setBounds(1050, 15, 100,25);
        credits.setBounds(0, mainFrame.getHeight() - 64, 1300, 35);
        eraseDataButton.setBounds(1020, credits.getY() - 30, eraseDataButton.getText().length() * 15,25);

        // pushing
        mainFrame.add(insert);
        mainFrame.add(insertTitle);
        mainFrame.add(submitButton);
        mainFrame.add(scrollPane);
        mainFrame.add(langButton);
        mainFrame.add(credits);
        mainFrame.add(eraseDataButton);


        // styles
        insert.setBackground(Color.white);
        insert.setForeground(Color.black);
        credits.setOpaque(true);
        credits.setBackground(Color.black);
        credits.setForeground(Color.white);
        insert.setBorder(BorderFactory.createLineBorder(Color.orange));
        mainFrame.getContentPane().setBackground(Color.WHITE);
        insert.setFont(new Font("Serif", Font.PLAIN, 20));
        insertTitle.setFont(new Font("Serif", Font.BOLD, 25));
        submitButton.setFont(new Font("Serif", Font.BOLD, 20));
        submitButton.setFocusPainted(false);
        submitButton.setContentAreaFilled(false);
        langButton.setFont(new Font("Serif", Font.BOLD, 10));
        langButton.setFocusPainted(false);
        langButton.setContentAreaFilled(false);
        langButton.setMargin(new Insets(0, 0, 0, 0));

        eraseDataButton.setFont(new Font("Serif", Font.BOLD, 20));
        eraseDataButton.setFocusPainted(false);
        eraseDataButton.setContentAreaFilled(false);
        eraseDataButton.setForeground(Color.RED);
        eraseDataButton.setBorder(BorderFactory.createLineBorder(Color.RED));

        //table styles
        resultTable.setFont(new Font("Serif", Font.PLAIN, 17));
        resultTable.setRowHeight(20);
        resultTable.setRowMargin(5);

        mainFrame.repaint();
    }

    public static void showStatusMsg(String msg, boolean isError) {
        JLabel info = new JLabel(msg);
        info.setBounds(150, 90, info.getText().length() * 10,30);
        info.setHorizontalAlignment(JLabel.CENTER);
        
        info.setBorder(BorderFactory.createLineBorder(isError == true ? Color.RED : Color.GREEN, 5));

        mainFrame.add(info);
        mainFrame.repaint();

        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    mainFrame.remove(info);
                    mainFrame.repaint();
                }
            }, 1500
    );
    }
}