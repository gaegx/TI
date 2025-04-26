import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class MainForm extends JFrame {
    private byte[] decrypted;
    private byte[] encrypted;

    private JTextField numP, numQ, numE, numD, lblRValue, lblFValue;
    private JTextArea txtIn, txtOut;
    private JButton btnCalculate, btnOpenEncrypt, btnOpenDecrypt, btnSaveDec;
    private JFileChooser fileChooser;

    public MainForm() {
        setTitle("RSA-like Encryption Tool");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Главная панель с отступами
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(mainPanel);

        // 1. Панель параметров (сетка 3x4 с заголовком)
        JPanel paramsPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        paramsPanel.setBorder(BorderFactory.createTitledBorder("Параметры RSA"));

        paramsPanel.add(createLabel("P (простое):"));
        numP = createTextField("61");
        paramsPanel.add(numP);

        paramsPanel.add(createLabel("Q (простое):"));
        numQ = createTextField("53");
        paramsPanel.add(numQ);

        paramsPanel.add(createLabel("E (открытая):"));
        numE = createTextField("17");
        paramsPanel.add(numE);

        paramsPanel.add(createLabel("R = P*Q:"));
        lblRValue = createTextField("3233", false);
        paramsPanel.add(lblRValue);

        paramsPanel.add(createLabel("F(R) = (P-1)(Q-1):"));
        lblFValue = createTextField("3120", false);
        paramsPanel.add(lblFValue);

        paramsPanel.add(createLabel("D (закрытая):"));
        numD = createTextField("2753", false);
        paramsPanel.add(numD);

        // 2. Панель кнопок (FlowLayout с выравниванием по центру)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        btnCalculate = createButton("Вычислить параметры", "calc.png");
        btnOpenEncrypt = createButton("Зашифровать файл", "encrypt.png");
        btnOpenDecrypt = createButton("Расшифровать файл", "decrypt.png");
        btnSaveDec = createButton("Сохранить результат", "save.png");

        buttonPanel.add(btnCalculate);
        buttonPanel.add(btnOpenEncrypt);
        buttonPanel.add(btnOpenDecrypt);
        buttonPanel.add(btnSaveDec);

        // 3. Панель ввода/вывода (с разделителем)
        JSplitPane ioPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        ioPanel.setResizeWeight(0.5);
        ioPanel.setDividerLocation(450);
        ioPanel.setPreferredSize(new Dimension(900, 400)); // Увеличиваем высоту

        txtIn = createTextArea();
        txtOut = createTextArea();

        JScrollPane inScroll = new JScrollPane(txtIn);
        inScroll.setBorder(BorderFactory.createTitledBorder("Входные данные"));
        inScroll.setPreferredSize(new Dimension(450, 350)); // Явный размер
        JScrollPane outScroll = new JScrollPane(txtOut);
        outScroll.setBorder(BorderFactory.createTitledBorder("Результат"));
        outScroll.setPreferredSize(new Dimension(450, 350)); // Явный размер

        ioPanel.setLeftComponent(inScroll);
        ioPanel.setRightComponent(outScroll);

        // Собираем главную панель
        mainPanel.add(paramsPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(ioPanel, BorderLayout.SOUTH);

        // Инициализация
        fileChooser = new JFileChooser();
        customizeFileChooser();

        // Обработчики событий
        btnCalculate.addActionListener(this::calculateParameters);
        btnOpenEncrypt.addActionListener(this::openForEncryption);
        btnOpenDecrypt.addActionListener(this::openForDecryption);
        btnSaveDec.addActionListener(this::saveDecrypted);
    }

    // Вспомогательные методы для создания компонентов
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JTextField createTextField(String text) {
        return createTextField(text, true);
    }

    private JTextField createTextField(String text, boolean editable) {
        JTextField field = new JTextField(text);
        field.setEditable(editable);
        field.setHorizontalAlignment(JTextField.CENTER);
        return field;
    }

    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(180, 35));
        // Здесь можно добавить иконку: new ImageIcon(iconPath)
        return button;
    }

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Увеличили размер шрифта
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setRows(15); // Минимальное количество строк
        area.setColumns(60); // Минимальное количество колонок
        return area;
    }

    private void customizeFileChooser() {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setAcceptAllFileFilterUsed(false);
    }

    private void calculateParameters(ActionEvent e) {
        StringBuilder sb = new StringBuilder();
        int p, q, eVAl;

        try {
            p = Integer.parseInt(numP.getText());
            q = Integer.parseInt(numQ.getText());
            eVAl = Integer.parseInt(numE.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Некорректные числовые значения", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!CipMath.isPrime(p)) sb.append("|P должно быть простым|\n");
        if (!CipMath.isPrime(q)) sb.append("|Q должно быть простым|\n");

        int r = p * q;
        if (r < 256) sb.append("|P*Q должно быть больше 256|\n");
        else if (r > 65535) sb.append("|P*Q должно быть меньше 65,535|\n");

        if (sb.length() == 0) {
            lblRValue.setText(String.valueOf(r));
            int fe = CipMath.fEuler(p, q);
            lblFValue.setText(String.valueOf(fe));

            if (eVAl >= fe) sb.append("|E должно быть меньше F(R)|\n");
            else {
                int[] f = CipMath.getInverse(fe, eVAl);
                if (f[2] != 1) sb.append("|E и F(R) должны иметь НОД = 1|\n");
                else numD.setText(String.valueOf(f[1]));
            }
        } else {
            lblRValue.setText("Oops...");
            lblFValue.setText("Oops...");
        }

        if (sb.length() > 0) {
            JOptionPane.showMessageDialog(this, sb.toString(), "Ошибки", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void openForEncryption(ActionEvent e) {
        try {
            int r = Integer.parseInt(numP.getText()) * Integer.parseInt(numQ.getText());
            if (r <= 256 || r >= 65535) {
                JOptionPane.showMessageDialog(this, "P*Q должно быть в диапазоне 256..65535", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                encrypted = Files.readAllBytes(file.toPath());
                txtIn.setText(Converter.bytesToStr(encrypted));
                decrypted = Cipher.encode(encrypted, Integer.parseInt(numE.getText()), r);
                txtOut.setText(Converter.wordsToStr(decrypted));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openForDecryption(ActionEvent e) {
        try {
            int r = Integer.parseInt(numP.getText()) * Integer.parseInt(numQ.getText());
            if (r <= 256 || r >= 65535) {
                JOptionPane.showMessageDialog(this, "P*Q должно быть в диапазоне 256..65535", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                encrypted = Files.readAllBytes(file.toPath());
                txtIn.setText(Converter.wordsToStr(encrypted));
                decrypted = Cipher.decode(encrypted, Integer.parseInt(numD.getText()), r);
                txtOut.setText(Converter.bytesToStr(decrypted));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveDecrypted(ActionEvent e) {
        if (decrypted != null && fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Files.write(fileChooser.getSelectedFile().toPath(), decrypted);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка сохранения: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}