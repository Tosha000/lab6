package lab_6;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class FractalExplorer {
    // Поля для кнопки сохранения, кнопки сброса и коллекции для enableUI
    private JButton save_Button;
    private JButton reset_Button;
    private JComboBox Combo_Box;
    private int size;
    private JImageDisplay display;
    private FractalGenerator gen;
    private Rectangle2D.Double d2;
    private int rows_remaining;

    public FractalExplorer(int size) {
        this.size = size;
        gen = new Burning_Ship();
        d2 = new Rectangle2D.Double();
        gen.getInitialRange(d2);
        display = new JImageDisplay(size, size);

    }


    public void createAndShowGUI() {
        display.setLayout(new BorderLayout());
        JFrame JimageDisplay = new JFrame("Fractal Explorer");
        JimageDisplay.add(display, BorderLayout.CENTER);
        reset_Button = new JButton("Reset");
        ButtonHandler handler = new ButtonHandler();
        reset_Button.addActionListener(handler);
        JimageDisplay.add(reset_Button, BorderLayout.SOUTH);

        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);

        // Операция закрытия окна по умолчанию:
        JimageDisplay.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Combo_Box = new JComboBox();
        FractalGenerator mandelbrot = new Mandelbrot();
        Combo_Box.addItem(mandelbrot);
        FractalGenerator tricorn = new Tricorn();
        Combo_Box.addItem(tricorn);
        FractalGenerator burning_Ship = new Burning_Ship();
        Combo_Box.addItem(burning_Ship);
        //Создаем кнопку для выбора фрактала из коллекции
        ButtonHandler fractalChooser = new ButtonHandler();
        Combo_Box.addActionListener(fractalChooser);
        JPanel DisplayPanel = new JPanel();
        JLabel myLabel = new JLabel("Fractal:");
        DisplayPanel.add(myLabel);
        DisplayPanel.add(Combo_Box);
        JimageDisplay.add(DisplayPanel, BorderLayout.NORTH);
        //Создаем кнопку для сохранения изображения фрактала
        save_Button = new JButton("Save");
        JPanel myBottomPanel = new JPanel();
        myBottomPanel.add(save_Button);
        myBottomPanel.add(reset_Button);
        JimageDisplay.add(myBottomPanel, BorderLayout.SOUTH);

        ButtonHandler saveHandler = new ButtonHandler();
        save_Button.addActionListener(saveHandler);


        JimageDisplay.pack();
        JimageDisplay.setVisible(true);
        JimageDisplay.setResizable(false);
    }



    private void drawFractal() {
        // Метод для вывода на экран фрактала, должен циклически проходить через каждый пиксель в отображении (т.е. значения x и y будут меняться от 0 до размера отображения)
        // Необходим, чтобы отключить все элементы управления пользовательского интерфейса во время рисования
        enableUI(false);
        // Установим общее количество оставшихся строк
        rows_remaining = size;
        // Переберём каждую строку и вызовем перерисовку
        for (int x=0; x<size; x++) {
            FractalWorker draw = new FractalWorker(x);
            draw.execute();
        }
    }
    // Метод включает или отключает кнопки с коллекцией в пользовательском
    // интерфейсе на основе указанного параметра
    private void enableUI(boolean val) {
        Combo_Box.setEnabled(val);
        reset_Button.setEnabled(val);
        save_Button.setEnabled(val);
    }



    // Внутренний класс для обработки событий от кнопок
    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // Получение команды
            String command = e.getActionCommand();
            // Если команда - получить выпадающий список, то список выпадает, пользователь выбирает фрактал и он перерисовывается
            if (e.getSource() instanceof JComboBox) {
                JComboBox mySource = (JComboBox) e.getSource();
                gen = (FractalGenerator) mySource.getSelectedItem();
                gen.getInitialRange(d2);
                drawFractal();
            }
            // Если команда сбросить фрактал - сбросить его и перерисовать
            else if (command.equals("Reset")) {
                gen.getInitialRange(d2);
                drawFractal();
            }
            // Если команда сохранить фрактал - сохранить его ф ормате PNG на диск
            else if (command.equals("Save")) {
                JFileChooser chooser = new JFileChooser();
                // Сохранять только в формате PNG
                FileFilter extensionFilter = new FileNameExtensionFilter("PNG Images", "png");
                chooser.setFileFilter(extensionFilter);
                // Если файл хотят сохранить в формате не png, вернуть false
                chooser.setAcceptAllFileFilterUsed(false);

                // Значение типа int, которое указывает результат операции выбора файла
                int userSelection = chooser.showSaveDialog(display);

                //Если метод возвращает значение JfileChooser.APPROVE_OPTION, тогда можно продолжить операцию
                // сохранения файлов, в противном случае, если пользователь отменил операцию,
                // закончить данную обработку события без сохранения
                if (userSelection == JFileChooser.APPROVE_OPTION) {

                    //Класс javax.imageio.ImageIO обеспечивает простые операции загрузки и сохранения изображения
                    java.io.File file = chooser.getSelectedFile();
                    // Попытка сохранить фрактал на диск
                    try {
                        BufferedImage displayImage = display.getImage();
                        javax.imageio.ImageIO.write(displayImage, "png", file);
                    }
                    // Проинформировать пользователя об ошибке через диалоговое окно
                    catch (Exception exception) {
                        JOptionPane.showMessageDialog(display, exception.getMessage(), "Cannot Save Image", JOptionPane.ERROR_MESSAGE);
                    }
                }
                // Если операция сохранения файла не APPROVE_OPTION
                else return;
            }
        }
    }

    // Внутренний класс для обработки событий от кнопки сброса. Обработчик сбрасывает
    // диапазон к начальному, определенному генератором, а затем перерисовает фрактал
    private class ResetHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            gen.getInitialRange(d2);
            drawFractal();
        }
    }

    // Внутренний класс для обработки событий с дисплея от мыши
    // Внутренний класс для обработки событий с дисплея от мыши
    private class MouseHandler extends MouseAdapter {
        @Override // Переопределим метод
        // При получении события о щелчке мышью, класс должен
        // отобразить пиксельные кооринаты щелчка в область фрактала, а затем вызвать
        // метод генератора recenterAndZoomRange с координатами, по которым щелкнули, и масштабом 0.5, что приведёт к увеличению фрактала
        public void mouseClicked(MouseEvent e) {
            // Вернуться, если количество оставшихся строк не равно 0
            if (rows_remaining != 0) {
                return;
            }
            // Получение координаты х области щелчка мыши
            int x = e.getX();
            double xCoord =gen.getCoord(d2.x, d2.x + d2.width, size, x);
            // Получение координаты у области щелчка мыши
            int y = e.getY();
            double yCoord = gen.getCoord(d2.y, d2.y + d2.height, size, y);
            // Увеличение фрактала
            gen.recenterAndZoomRange(d2, xCoord, yCoord, 0.5);
            // Перерисуем фрактал
            drawFractal();
        }
    }

    private class FractalWorker extends SwingWorker<Object, Object> {
        int y;
        int[] mas;

        FractalWorker(int y) {
            this.y = y;
        }

        protected Object doInBackground() {
            mas = new int[size];
            for (int i=0; i< mas.length; i++){
                double xCoord = gen.getCoord(d2.x, d2.x + d2.width, size, i);
                double yCoord = gen.getCoord(d2.y, d2.y + d2.height, size, y);
                // Вычислим количество итераций для соответствующих координат в области отображения фрактала
                int iteration = gen.numIterations(xCoord, yCoord);

                if (iteration == -1) { // Если число итераций равно -1 (т.е. точка не выходит за границы),установим пиксель в черный цвет (для rgb значение 0).
                    mas[i] = 0;

                } else { // Иначе выберем значение цвета, основанное на количестве итераций
                    // Воспользуемся цветовым пространством HSV: поскольку значение цвета
                    // варьируется от 0 до 1, получается плавная последовательность цветов от
                    // красного к желтому, зеленому, синему, фиолетовому и затем обратно к красному
                    float hue = 0.7f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    mas[i] = rgbColor;
                }
            }


            return null;
        }
        protected void done() {
            for (int i = 0; i < mas.length; i++) {
                display.drawPixel(i, y, mas[i]);
            }
            display.repaint(0, 0, y, size, 1); // Метод, который позволяет указать область для перерисовки фрактала
            rows_remaining--;
            if (rows_remaining== 0) {
                enableUI(true);
            }
        }
    }

    public static void main(String[] args)
    {
        FractalExplorer explorer = new FractalExplorer(500);
        explorer.createAndShowGUI();
        explorer.drawFractal();
    }
}