package bsu.rfe.java.group10.lab4.Yaroshevich.varC2;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class MainFrame extends JFrame {

    // Начальные размеры окна
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    // Объект диалогового окна для выбора файлов
    private JFileChooser fileChooser = null;

    // Пункты меню
    private JCheckBoxMenuItem showAxisMenuItem;
    private JCheckBoxMenuItem showMarkerMenuItem;

    // Компонент-отображатель графика
    private GraphicsDisplay display = new GraphicsDisplay();

    // Флаг, указывающей на загруженность данных графика
    private boolean fileLoaded = false;

    //Реализация конструктора окна MainFrame()
    public MainFrame(){
        // Вызов конструктора предка Frame
        super("Построение графиков функций на основе подготовленных файлов");
        // Установка размеров окна
        setSize(WIDTH,HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        // Отцентрировать окно приложения на экране
        setLocation((kit.getScreenSize().width-WIDTH)/2,
                (kit.getScreenSize().height-HEIGHT)/2);
        // Развёртывания окна на весь экран
        setExtendedState(MAXIMIZED_BOTH);

        // Конструирования полосы меню
        // Создать и установить полосу меню
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        // Добавить пункт "файл"
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        // Создать действие по открытию файла
        Action openGraphicsAction = new AbstractAction("Открыть файл") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileChooser==null){
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if(fileChooser.showOpenDialog(MainFrame.this)==JFileChooser.APPROVE_OPTION)
                    openGraphics(fileChooser.getSelectedFile());
            }
        };
        // Добавить соответсвубщий пункт меню
        fileMenu.add(openGraphicsAction);
        // Создать пункт меню "График"
        JMenu graphicsMenu = new JMenu("График");
        menuBar.add(graphicsMenu);
        // Создать действие для реакции на активацию элемента
        // "Показывать оси координат"
        Action showAxisAction = new AbstractAction("Показывать оси координат") {
            @Override
            public void actionPerformed(ActionEvent e) {
                // свойство showAxis класса GraphicsDisplay истина,
                // если элемент меню showAxisMenuItem отмечен флажком,
                // ложь - в противном случае
                display.setShowAxis(showAxisMenuItem.isSelected());
            }
        };
        showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction);
        graphicsMenu.add(showAxisMenuItem);
        showAxisMenuItem.setSelected(true);
        // Создать тоже самое для "Показывать маркеры точек"
        Action showMarkersAction = new AbstractAction("Показывать маркеры точек") {
            @Override
            public void actionPerformed(ActionEvent e) {
                display.setShowMarkers(showMarkerMenuItem.isSelected());
            }
        };
        showMarkerMenuItem = new JCheckBoxMenuItem(showMarkersAction);
        graphicsMenu.add(showMarkerMenuItem);
        // Элемент по умолчанию выключен
        showMarkerMenuItem.setSelected(false);
        // Зарегистрировать обработчик событий, связаный с меню "График"
        graphicsMenu.addMenuListener(new GraphicsMenuListener());
        // Установить GraphicsDisplay в центр граничной компоновки
        getContentPane().add(display,BorderLayout.CENTER);
    }

    // Считывания данных графика из существующего файла
    protected void openGraphics(File selectedFile){
        try{
            // Шаг 1 - открыть поток чтения данных, связанный с файлом
            DataInputStream in = new DataInputStream(new FileInputStream(selectedFile));
            /* Шаг 2 - Зная объём данных в потоке ввода можно вычислить,
            сколько памяти нужно зарезервировать в массиве:
            Всего байт в потоке - in.available() байт;
            Размер числа Double - Double.SIZE бит, или Double.SIZE/8 в байт;
            Так как числа записываються парами, то число пар меньше в 2 раза
             */
            Double[][] graphicsData = new Double[in.available()/(Double.SIZE/8)/2][];
            // Шаг 3 - Цикл чтения данных (пока в потоке есть данные)
            int i=0;
            while (in.available()>0){
                // Первой из потока читаеться координата точки X
                Double x = in.readDouble();
                // Затем - значения графика Y в точке X
                Double y = in.readDouble();
                // Прочитанная пара координат добавляеться в массив
                graphicsData[i++] = new Double[]{x,y};
            }
            // Шаг 4 - Проверка, имееться ли в списке в результате чтения хотя бы одна пара координат
            if(graphicsData!=null && graphicsData.length>0){
                // Да - установить флаг загруженности данных
                fileLoaded = true;
                // Вызывать метод флаг отображения графика
                display.showGraphics(graphicsData);
            }
            // Шаг 5 - Закрыть входной поток
            in.close();

        }catch (FileNotFoundException ex){
            // В случае исключительной ситуации типа "Файл не найден"
            // показать сообщение об ошибке
            JOptionPane.showMessageDialog(MainFrame.this,
                    "Указанный метод не найден","Ощибка загрузки",JOptionPane.WARNING_MESSAGE);
            return;
        }
        catch (IOException ex){
            // В случае ошибки ввода из файлового потока
            // Показать сообщение об ошибке
            JOptionPane.showMessageDialog(MainFrame.this,
                    "Ошибка чтения координат точек из файла","Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE);
            return;

        }
    }






















    // Реализация класса GraphicsMenuListener являющийся внутреним
    // классом слушателем событий и реализует интерфейс MenuListener
    private class GraphicsMenuListener implements MenuListener{

        // Обработчик, вызываемый перед показом меню
        public void menuSelected(MenuEvent e){
            // Доступность или недоступность элементов меню "График"
            // определяться загруженностью данных
            showAxisMenuItem.setEnabled(fileLoaded);
            showMarkerMenuItem.setEnabled(fileLoaded);
        }

        // Обработчик, вызываемый после того, как меню исчезло с экрана
        public void menuDeselected(MenuEvent e){
        }

        // Обработчик, вызываемый в случае отмены выбора пункта меню
        // (очень редкая ситуация)
        public void menuCanceled(MenuEvent e){
        }
    }
}
