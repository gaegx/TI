import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class Main {

    private JFrame frame;
    private JTextArea inputArea;
    private JTextField keyField;
    private JTextArea outputArea;
    private JButton encryptButton, decryptButton;
    private JRadioButton vigenereRadio, railFenceRadio;

    public Main() {
        frame = new JFrame("Шифр");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem openItem = new JMenuItem("Открыть...");
        JMenuItem saveItem = new JMenuItem("Сохранить как...");
        openItem.addActionListener(this::handleOpen);
        saveItem.addActionListener(this::handleSave);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        // Ввод
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Входной текст"));
        inputArea = new JTextArea(5, 40);
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);

        // Ключ
        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        keyPanel.setBorder(BorderFactory.createTitledBorder("Ключ / Кол-во рельс"));
        keyField = new JTextField(20);
        keyPanel.add(new JLabel("Ключ:"));
        keyPanel.add(keyField);

        // Выбор шифра
        JPanel cipherPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cipherPanel.setBorder(BorderFactory.createTitledBorder("Выбор шифра"));
        vigenereRadio = new JRadioButton("Виженер", true);
        railFenceRadio = new JRadioButton("Железнодорожный");
        ButtonGroup cipherGroup = new ButtonGroup();
        cipherGroup.add(vigenereRadio);
        cipherGroup.add(railFenceRadio);
        cipherPanel.add(vigenereRadio);
        cipherPanel.add(railFenceRadio);

        // Кнопки
        JPanel buttonPanel = new JPanel();
        encryptButton = new JButton("Зашифровать");
        decryptButton = new JButton("Расшифровать");
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        // Вывод
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Результат"));
        outputArea = new JTextArea(5, 40);
        outputArea.setEditable(false);
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Добавление компонентов
        JPanel centerPanel = new JPanel(new GridLayout(3, 1));
        centerPanel.add(keyPanel);
        centerPanel.add(cipherPanel);
        centerPanel.add(buttonPanel);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(outputPanel, BorderLayout.SOUTH);

        encryptButton.addActionListener(this::handleEncrypt);
        decryptButton.addActionListener(this::handleDecrypt);

        frame.setVisible(true);
    }

    private void handleEncrypt(ActionEvent e) {
        String text = inputArea.getText().toUpperCase().replaceAll("[^А-ЯЁ]", "");
        String key = keyField.getText().toUpperCase().replaceAll("[^А-ЯЁ0-9]", "");

        if (text.isEmpty() || key.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Введите текст и ключ/число", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result;
        if (vigenereRadio.isSelected()) {
            result = VigenereCipher.encrypt(text, key);
        } else {
            try {
                int rails = Integer.parseInt(key);
                result = RailwayCipher.encrypt(text, rails);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Для железнодорожного шифра введите число", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        outputArea.setText(result);
    }

    private void handleDecrypt(ActionEvent e) {
        String text = inputArea.getText().toUpperCase().replaceAll("[^А-ЯЁ]", "");
        String key = keyField.getText().toUpperCase().replaceAll("[^А-ЯЁ0-9]", "");

        if (text.isEmpty() || key.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Введите текст и ключ/число", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result;
        if (vigenereRadio.isSelected()) {
            result = VigenereCipher.decrypt(text, key);
        } else {
            try {
                int rails = Integer.parseInt(key);
                result = RailwayCipher.decrypt(text, rails);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Для железнодорожного шифра введите число", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        outputArea.setText(result);
    }

    private void handleOpen(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                inputArea.setText("");  // Очистить поле ввода перед загрузкой нового текста
                String line;
                while ((line = reader.readLine()) != null) {
                    inputArea.append(line);
                    inputArea.append("\n");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Ошибка чтения файла", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void handleSave(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(outputArea.getText());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Ошибка сохранения файла", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
} 