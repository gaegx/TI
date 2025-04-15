import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Files;

public class CipherGUI {
    private static final int MAX_DISPLAY_LENGTH = 50;
    private final Cipher cipher = new Cipher();
    private final Coder coder = new Coder();

    private byte[] originalData;
    private byte[] encryptedData;
    private byte[] keyStream;

    private JFrame frame;
    private JTextArea originalTextArea;
    private JTextArea keyStreamTextArea;
    private JTextArea encryptedTextArea;
    private JTextField keyTextField;
    private JLabel keyStatusLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CipherGUI().createAndShowGUI());
    }

    void createAndShowGUI() {
        frame = new JFrame("Шифрование LFSR");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Панель ввода ключа
        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        keyPanel.add(new JLabel("Ключ (34 бит):"));

        keyTextField = new JTextField(34);
        keyTextField.getDocument().addDocumentListener(new KeyDocumentListener());
        keyPanel.add(keyTextField);

        keyStatusLabel = new JLabel("0/34");
        keyPanel.add(keyStatusLabel);

        // Панель с текстовыми областями
        JPanel textAreasPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        originalTextArea = createTextArea("Исходные данные");
        keyStreamTextArea = createTextArea("Поток ключа");
        encryptedTextArea = createTextArea("Зашифрованные данные");

        textAreasPanel.add(createScrollPane(originalTextArea));
        textAreasPanel.add(createScrollPane(keyStreamTextArea));
        textAreasPanel.add(createScrollPane(encryptedTextArea));

        // Панель кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JButton encryptButton = createButton("Кодировать", this::encrypt);
        JButton openButton = createButton("Открыть файл", this::openFile);
        JButton saveButton = createButton("Сохранить файл", this::saveFile);

        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(encryptButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(openButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createVerticalGlue());

        mainPanel.add(keyPanel, BorderLayout.NORTH);
        mainPanel.add(textAreasPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.EAST);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JTextArea createTextArea(String title) {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createTitledBorder(title));
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        return textArea;
    }

    private JScrollPane createScrollPane(JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 500));
        return scrollPane;
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(150, 40));
        button.setMaximumSize(new Dimension(150, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.addActionListener(listener);
        return button;
    }

    private void encrypt(ActionEvent e) {
        String keyText = keyTextField.getText();

        if (keyText.length() != 34) {
            JOptionPane.showMessageDialog(frame, "Ключ должен быть 34 бит длиной!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!keyText.matches("[01]+")) {
            JOptionPane.showMessageDialog(frame, "Ключ должен содержать только 0 и 1!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (originalData == null) {
            JOptionPane.showMessageDialog(frame, "Нет данных для шифрования!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Создаем копию оригинальных данных, чтобы не потерять их
            byte[] dataToEncrypt = originalData.clone();

            long key = Long.parseLong(keyText, 2);
            keyStream = cipher.encrypt(dataToEncrypt, key);
            encryptedData = new byte[dataToEncrypt.length];

            for (int i = 0; i < dataToEncrypt.length; i++) {
                encryptedData[i] = (byte)(dataToEncrypt[i] ^ keyStream[i]);
            }
            encryptedData=dataToEncrypt.clone();
            displayData(); // например, показать encryptedData

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Ошибка преобразования ключа!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void openFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                originalData = Files.readAllBytes(fileChooser.getSelectedFile().toPath());
                displayData();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Ошибка чтения файла: " + ex.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile(ActionEvent e) {
        if (encryptedData == null) {
            JOptionPane.showMessageDialog(frame, "Нет данных для сохранения!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.write(fileChooser.getSelectedFile().toPath(), encryptedData);
                JOptionPane.showMessageDialog(frame, "Файл успешно сохранен!",
                        "Сохранение", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Ошибка сохранения файла: " + ex.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayData() {
        originalTextArea.setText(formatData(originalData));
        keyStreamTextArea.setText(formatData(keyStream));
        encryptedTextArea.setText(formatData(encryptedData));
    }

    private String formatData(byte[] data) {
        if (data == null) return "";

        if (data.length <= MAX_DISPLAY_LENGTH) {
            return coder.bytesToBinary(data);
        } else {
            byte[] firstPart = new byte[MAX_DISPLAY_LENGTH/2];
            byte[] lastPart = new byte[MAX_DISPLAY_LENGTH/2];
            System.arraycopy(data, 0, firstPart, 0, firstPart.length);
            System.arraycopy(data, data.length - lastPart.length, lastPart, 0, lastPart.length);
            return coder.bytesToBinary(firstPart) + "\n...\n" + coder.bytesToBinary(lastPart);
        }
    }

    private class KeyDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateKeyStatus();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateKeyStatus();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateKeyStatus();
        }

        private void updateKeyStatus() {
            String text = keyTextField.getText();
            int count = text.replaceAll("[^01]", "").length();
            keyStatusLabel.setText(count + "/34");
        }
    }
}