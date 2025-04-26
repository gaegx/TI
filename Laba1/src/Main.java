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
        String keyInput = keyField.getText().toUpperCase();

        if (text.isEmpty() || keyInput.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Введите текст и ключ/число", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result;
        if (vigenereRadio.isSelected()) {
            String key = keyInput.replaceAll("[^А-ЯЁ]", ""); // оставляем только буквы
            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Ключ для шифра Виженера должен содержать только буквы", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            result = VigenereCipher.encrypt(text, key);
        } else {
            String digits = keyInput.replaceAll("[^0-9]", ""); // оставляем только цифры
            if (digits.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Для железнодорожного шифра введите число", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int rails = Integer.parseInt(digits);
            result = RailwayCipher.encrypt(text, rails);
        }

        outputArea.setText(result);
    }

    private void handleDecrypt(ActionEvent e) {
        String text = inputArea.getText().toUpperCase().replaceAll("[^А-ЯЁ]", "");
        String keyInput = keyField.getText().toUpperCase();

        if (text.isEmpty() || keyInput.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Введите текст и ключ/число", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result;
        if (vigenereRadio.isSelected()) {
            String key = keyInput.replaceAll("[^А-ЯЁ]", ""); // оставляем только буквы
            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Ключ для шифра Виженера должен содержать только буквы", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            result = VigenereCipher.decrypt(text, key);
        } else {
            String digits = keyInput.replaceAll("[^0-9]", ""); // оставляем только цифры
            if (digits.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Для железнодорожного шифра введите число", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int rails = Integer.parseInt(digits);
            result = RailwayCipher.decrypt(text, rails);
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