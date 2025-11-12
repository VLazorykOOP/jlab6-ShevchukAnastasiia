import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * ВЛАСНЕ ВИКЛЮЧЕННЯ (Вимога №3)
 * Наслідується від ArithmeticException.
 * Генерується, якщо сума головної діагоналі матриці дорівнює нулю.
 */
class MatrixDiagonalSumException extends ArithmeticException {
    public MatrixDiagonalSumException(String message) {
        super(message);
    }
}

/**
 * Головний клас програми, що створює GUI.
 * Використовує JFrame, JPanel, JButton, JLabel, JTextField, JTable.
 */
public class MatrixGUI extends JFrame {

    // --- Поля класу (компоненти GUI) ---
    private JTextField filenameField;
    private JButton loadButton;
    private JTable originalTable;
    private JTable resultTable;
    private JLabel statusLabel;

    // --- Поля для даних ---
    private int[][] originalMatrix;
    private int[][] resultMatrix;
    private int n; // Розмір матриці

    /**
     * Конструктор, що ініціалізує GUI
     */
    public MatrixGUI() {
        // --- Налаштування головного вікна (JFrame) ---
        setTitle("Обробка матриці (Лабораторна робота)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Вікно по центру
        setLayout(new BorderLayout(5, 5)); // 5px відступи

        // --- Ініціалізація компонентів ---
        initTopPanel();    // Панель для вводу файлу
        initCenterPanel(); // Панель для двох таблиць
        initBottomPanel(); // Панель для статусу
    }

    /**
     * Створює верхню панель з полем вводу та кнопкою
     */
    private void initTopPanel() {
        // JPanel - контейнер для інших компонентів
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        // JLabel - простий текстовий напис
        topPanel.add(new JLabel("Ім'я файлу:"));

        // JTextField - поле для вводу тексту
        filenameField = new JTextField("input.txt", 20);
        topPanel.add(filenameField);

        // JButton - кнопка
        loadButton = new JButton("Завантажити та Обробити");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Цей метод викликається при натисканні кнопки
                processFile();
            }
        });
        topPanel.add(loadButton);

        // Додаємо верхню панель у "ПІВНІЧНУ" частину вікна
        add(topPanel, BorderLayout.NORTH);
    }

    /**
     * Створює центральну панель з двома JTable
     */
    private void initCenterPanel() {
        // Головна центральна панель з сіткою 1x2 (1 рядок, 2 стовпці)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // JTable - компонент для відображення табличних даних
        originalTable = new JTable();
        resultTable = new JTable();

        // Створюємо ліву панель для початкової матриці
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftPanel.add(new JLabel("Початкова матриця", SwingConstants.CENTER), BorderLayout.NORTH);
        // JScrollPane дозволяє прокручувати таблицю, якщо вона велика
        leftPanel.add(new JScrollPane(originalTable), BorderLayout.CENTER);

        // Створюємо праву панель для матриці-результату
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        rightPanel.add(new JLabel("Результат (циклічний зсув)", SwingConstants.CENTER), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);

        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);

        // Додаємо центральну панель в "ЦЕНТРАЛЬНУ" частину вікна
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Створює нижню панель (статус-бар)
     */
    private void initBottomPanel() {
        // JLabel, який буде показувати помилки або успіх
        statusLabel = new JLabel("Статус: Готовий до завантаження файлу.");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.LIGHT_GRAY);

        // Додаємо статус-бар в "ПІВДЕННУ" частину вікна
        add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * Головний метод, що запускається по кнопці.
     * Містить логіку читання файлу та обробку ВСІХ винятків.
     */
    private void processFile() {
        String filename = filenameField.getText();
        if (filename.isEmpty()) {
            statusLabel.setText("Помилка: Ім'я файлу не може бути порожнім.");
            return;
        }

        try {
            // 1. --- Читання файлу ---
            Scanner sc = new Scanner(new File(filename));

            // 2. --- Читання розміру ---
            if (!sc.hasNextInt()) {
                throw new NumberFormatException("Файл має починатися з розміру матриці (n).");
            }
            n = sc.nextInt();

            // 3. --- Читання матриці ---
            originalMatrix = new int[n][n];
            int diagonalSum = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (!sc.hasNextInt()) {
                        throw new NumberFormatException("Невірний формат даних у файлі. Очікувалося " + (n * n) + " чисел.");
                    }
                    originalMatrix[i][j] = sc.nextInt();
                    if (i == j) {
                        diagonalSum += originalMatrix[i][j];
                    }
                }
            }
            sc.close();

            // 4. --- ПЕРЕВІРКА НА ВЛАСНЕ ВИКЛЮЧЕННЯ (Вимога №3) ---
            if (diagonalSum == 0) {
                throw new MatrixDiagonalSumException("Сума елементів головної діагоналі дорівнює 0!");
            }

            // 5. --- Відображення початкової матриці ---
            updateTable(originalTable, originalMatrix, n);

            // 6. --- Виконання логіки обробки ---
            resultMatrix = performMatrixLogic(originalMatrix, n);

            // 7. --- Відображення результату ---
            updateTable(resultTable, resultMatrix, n);

            statusLabel.setText("Статус: Файл '" + filename + "' успішно оброблено.");

        }
        // --- ОБРОБКА ВИКЛЮЧЕНЬ ---
        catch (FileNotFoundException e) {
            // СТАНДАРТНИЙ ВИКЛЮЧОК №1
            statusLabel.setText("Помилка: Файл не знайдено. " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Файл '" + filename + "' не знайдено.", "Помилка файлу", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException | NoSuchElementException e) {
            // СТАНДАРТНИЙ ВИКЛЮЧОК №2 (об'єднано два типи помилок формату)
            statusLabel.setText("Помилка: Невірний формат даних у файлі. " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Невірний формат даних у файлі.\nПеревірте, що всі дані є числами.", "Помилка формату", JOptionPane.ERROR_MESSAGE);
        } catch (MatrixDiagonalSumException e) {
            // ВЛАСНИЙ ВИКЛЮЧОК №3
            statusLabel.setText("Помилка: " + e.getMessage());
            JOptionPane.showMessageDialog(this, e.getMessage(), "Власне виключення", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Інші можливі помилки
            statusLabel.setText("Загальна помилка: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Сталася неочікувана помилка: " + e.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Оновлює дані в JTable на основі 2D-масиву
     */
    private void updateTable(JTable table, int[][] data, int n) {
        // Назви стовпців
        String[] columnNames = new String[n];
        for (int i = 0; i < n; i++) {
            columnNames[i] = "Col " + (i + 1);
        }

        // JTable вимагає дані у форматі Object[][]
        Object[][] tableData = new Object[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tableData[i][j] = data[i][j]; // Автоматичне "упакування" int в Integer
            }
        }

        // DefaultTableModel - це стандартна модель для JTable
        DefaultTableModel model = new DefaultTableModel(tableData, columnNames);
        table.setModel(model);
    }

    /**
     * ВАША логіка з лабораторної роботи.
     * Приймає початкову матрицю і повертає НОВУ оброблену матрицю.
     */
    private int[][] performMatrixLogic(int[][] originalA, int n) {
        // 1. Створюємо ГЛИБОКУ КОПІЮ, щоб не змінити оригінал
        int[][] A = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(originalA[i], 0, A[i], 0, n);
        }

        // 2. Вставляємо ВАШ код (без введення/виведення)

        // Знаходимо стовпець з макс. сумою модулів (ця частина не використовується
        // для зсуву, але була у вашому коді, тому я її залишив)
        int maxSumCol = 0;
        int maxSum = Integer.MIN_VALUE;
        for (int j = 0; j < n; j++) {
            int sum = 0;
            for (int i = 0; i < n; i++) {
                sum += Math.abs(A[i][j]);
            }
            if (sum > maxSum) {
                maxSum = sum;
                maxSumCol = j;
            }
        }

        // (ця частина теж не використовується, але була у вас)
        int minInCol = A[0][maxSumCol];
        for (int i = 1; i < n; i++) {
            if (A[i][maxSumCol] < minInCol) {
                minInCol = A[i][maxSumCol];
            }
        }

        // Знаходимо стовпець з мінімальним елементом ВСІЄЇ матриці
        int minVal = A[0][0];
        int minCol = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (A[i][j] < minVal) {
                    minVal = A[i][j];
                    minCol = j;
                }
            }
        }

        // Виконуємо циклічний зсув рядків
        // (Важливо: ми модифікуємо КОПІЮ, а не оригінал)
        for (int i = 0; i < n; ++i) {
            int[] temp = new int[n];
            for (int k = 0; k < n; ++k) {
                // Це циклічний зсув ВЛІВО на minCol позицій
                temp[k] = A[i][(minCol + k) % n];
            }
            // Копіюємо зсунутий рядок назад в матрицю A
            System.arraycopy(temp, 0, A[i], 0, n);
        }

        // 3. Повертаємо оброблену матрицю
        return A;
    }

    /**
     * Головний метод (main) для запуску програми
     */
    public static void main(String[] args) {
        // Запускаємо GUI в потоці обробки подій Swing (це правильний шлях)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MatrixGUI gui = new MatrixGUI();
                gui.setVisible(true);
            }
        });
    }
}