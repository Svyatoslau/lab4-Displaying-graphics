package bsu.rfe.java.group10.lab4.Yaroshevich.varC2;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;

public class GraphicsDisplay extends JPanel {
    // Список координат точек для построения графика
    private Double[][] graphicsData;

    // Флаговые переменные, задающие правила отображения графика
    private boolean showAxis = true;
    private boolean showMarkers = false;
    private boolean showCoordinateGrid = false;

    // Границы диапазона пространства, подлежащего отображения
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    // Используемый массштаб отображения
    private double scale;

    // Различные стили черчения линий
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private BasicStroke coordinateGridStrokeAlongX;
    private BasicStroke coordinateGridStrokeAlongY;

    // Шрифт отображения подписей к осям координат
    private  Font axisFont;
    private Font gridFont;

    private int numberOfDecimalPlaces=0;

    public GraphicsDisplay(){
        // Цвеет заднего фона отображения - белый
        setBackground(Color.WHITE);

        // Сконструировать необходимые объекты, используемые в рисовании
        // Перо для рисования графика
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND,10.0f,new float[]{3,1,1,1,2,1,1,1,3,1,},0.0f);
        // Перо для рисования осей координат
        axisStroke = new BasicStroke(1.0f,BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,10.0f,null,0.0f);
        // Перо для риссования контуров маркером
        markerStroke = new BasicStroke(1.0f,BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,10.0f,null,0.0f);

        // Шрифт для подписей координат
        axisFont = new Font("Serif",Font.BOLD,36);
        gridFont = new Font("Serif",Font.BOLD,20);

    }

    // Метод вызываеться из обработчика элемента меню "Открыть файл с графиком"
    // главного окна приложения в случае успешной загрузки данных
    public void showGraphics(Double[][] graphicsData){
        // Сохранить массив точек во внутреннем поле класса
        this.graphicsData=graphicsData;
        // Запросить перерисовку компонента (неявно вызвать paintComponent())
        repaint();
    }

    // Методы модификаторы для изменения параметров отображения графика
    // Изменеие любого параметра приводит к перерисовки области
    public void setShowAxis(boolean showAxis){
        this.showAxis=showAxis;
        repaint();
    }
    public void setShowMarkers(boolean showMarkers){
        this.showMarkers=showMarkers;
        repaint();
    }
    public void setShowCoordinateGrid(boolean showCoordinateGrid){
        this.showCoordinateGrid=showCoordinateGrid;
        repaint();
    }
    // Методы помощники для преобразования координат
    protected Point2D.Double xyToPoint(double x,double y){
        // Вычисляем смещения от самой левой точки (minX)
        double deltaX = x-minX;
        // Вычисляем смещение Y от самой верхней точки (msxY)
        double deltaY = maxY-y;
        return new Point2D.Double(deltaX*scale,deltaY*scale);
    }
    // Получения точек экрана
    protected Point2D.Double shiftPoint(Point2D.Double src,double deltaX,double deltaY){
        // Инициализировать новый экземпляр точки
        Point2D.Double dest = new Point2D.Double();
        // Задать её координаты как координаты существующей точки + заданные смещения
        dest.setLocation(src.getX() + deltaX,src.getY()+deltaY);
        return dest;
    }

    //Реализация метода отображения линии графика paintGraphics()
    protected void paintGraphics(Graphics2D canvas){
        // Выбрать линию для рисования графика
        canvas.setStroke(graphicsStroke);
        // Выбрать цвет линии
        canvas.setColor(Color.RED);
        /* Будем рисвоть линию графика как путь, сотоящий из множества
        сегментова (GeneralPath). Начало пути устанавливаеться в первую точку
        графика, после чего прямой соединяеться со следующими точками*/
        GeneralPath graphics = new GeneralPath();
        for(int i=0; i<graphicsData.length;i++){
            // Преобразовать значения (x,y) в точку на экране point
            Point2D.Double point = xyToPoint(graphicsData[i][0],graphicsData[i][1]);
            if(i>0){
                // Не первая итерация - вести линию в точку point
                graphics.lineTo(point.getX(),point.getY());
            } else{
                // Первая итерация - установить начало пути в точку point
                graphics.moveTo(point.getX(),point.getY());
            }
        }
        // Отобразить график
        canvas.draw(graphics);
    }

    // Реализация мметода отобрадения осей координат paintAxis()
    protected void paintAxis(Graphics2D canvas){
        // Шаг 1 - установить необходимые настройки рисования
        // Установить особое начертания для осей
        canvas.setStroke(axisStroke);
        // Оси рисуються черным цветом
        canvas.setColor(Color.BLACK);
        // Стрелки заливаються чёрным цветом
        canvas.setPaint(Color.BLACK);
        // Подписи к координатным осям делаються специальным шрифтом
        canvas.setFont(axisFont);
        // Создать объект контекста отображения текста - для получения
        // характеристик устройства (экрана)
        FontRenderContext context = canvas.getFontRenderContext();
        // Шаг 2 - определить, должна ли быть видна ось Y на графике
        if(minX<=0.0 && maxX>=0.0){
            // Она видна, если левая граница показываемой области minX<=0.0,
            // а правая (maxX)>=0.0
            // Шаг 2а - ось Y - это линия между точками (0,maxY) и (0, minY)
            canvas.draw(new Line2D.Double(xyToPoint(0,maxY),xyToPoint(0,minY)));
            // Шаг 2б - Стрелки оси Y
            GeneralPath arrow = new GeneralPath();
            // Установить начальную точку ломанной точно на верхний конец оси Y
            Point2D.Double lineEnd = xyToPoint(0,maxY);
            arrow.moveTo(lineEnd.getX(),lineEnd.getY());
            // Вести левый "скат" стрелки в точку с относительными
            // координатами (5,20)
            arrow.lineTo(arrow.getCurrentPoint().getX()+5,
                    arrow.getCurrentPoint().getY()+20);
            // Вести нижнюю часть стрелки в точку с относительными
            // координатам (-10,0)
            arrow.lineTo(arrow.getCurrentPoint().getX()-10,arrow.getCurrentPoint().getY());
            // Замкнуть треуголник стрелки
            arrow.closePath();
            canvas.draw(arrow);// Нарисовать стрелку
            canvas.fill(arrow);// Закрасить стрелку
            // Шаг 2в - Нарисовать подписи к оси Y
            // Определить, сколько места понадобится для надписи "y"
            Rectangle2D bounds = axisFont.getStringBounds("y",context);
            Point2D.Double labelPos = xyToPoint(0,maxY);
            // Вывести надписи в точке с вычисленными координатами
            canvas.drawString("y",(float)labelPos.getX()+10,
                    (float)(labelPos.getY()-bounds.getY()));
        }
        // Шаг 3 - Определить, должна ли быть видна Ось X на графике
        if(minY<=0.0 && maxY>=0.0){
            // Она видна, если верхнняя граница показываемой области (maxY) >=0.0
            // а нижняя границ (minY) <= 0.0
            // Шаг 3а - ось x - это линия между точками (mimX,0) и (maxX,0)
            canvas.draw(new Line2D.Double(xyToPoint(minX,0),xyToPoint(maxX,0)));
            // Шаг 3б - Стрелка оси X
            GeneralPath arrow = new GeneralPath();
            // Установить ломанную на правый конец оси X
            Point2D.Double lineEnd =xyToPoint(maxX,0);
            arrow.moveTo(lineEnd.getX(),lineEnd.getY());
            // Вести верхний "скат" стрелки в точку с относительными
            // координатами (-20,-5)
            arrow.lineTo(arrow.getCurrentPoint().getX()-20,
                    arrow.getCurrentPoint().getY()-5);
            // Вести левую часть стрелки в точку
            // С относительными координатами (0,10)
            arrow.lineTo(arrow.getCurrentPoint().getX(),
                    arrow.getCurrentPoint().getY()+10);
            // Замкнуть треугольник стрелки
            arrow.closePath();
            canvas.draw(arrow); // Нарисовть стрелку
            canvas.fill(arrow); // Закрасить стрелку
            // Шаг 3в - Нарисовать подписи к оси X
            // Определить, сколько места понадобиться для надписи "x"
            Rectangle2D bounds = axisFont.getStringBounds("x",context);
            Point2D.Double labelPos = xyToPoint(maxX,0);
            canvas.drawString("x",(float)(labelPos.getX()-bounds.getWidth()-10),
                    (float)(labelPos.getY()+bounds.getY()));
        }
    }

    //Реализация метода отображения маркеров точек графика paintMarkers()
    protected void paintMarkers(Graphics2D canvas){
        // Шаг 1 - Установить специальное перо для черчения контуров маркеров
        canvas.setStroke(markerStroke);
        // Выбрать черрный цвет для конктуров маркеров

        for (Double[] point:graphicsData){
            // Инцилизировать маркер как кривую
            /* Специальное условие:
            В записи целой части значения функции
            в точке используются только чётные цифры (окрашу в СИНИЙ)
             */
            String y =  point[1].toString();
            int i=0;
            String number = "EVEN";
            // Проверка "есть ли нечётные цифры в целой части?"
            while(y.charAt(i)!='.'){
                if((y.charAt(i)-'0')%2==0 || y.charAt(i)=='-'){
                    i++;
                }else{
                    number="ODD";
                    break;
                }
            }
            switch (number) {
                case "EVEN": {
                    canvas.setColor(Color.BLUE);break;
                }
                case "ODD":{
                    canvas.setColor(Color.BLACK);break;
                }
            }
            GeneralPath marker = new GeneralPath();
            // Центр - в точке (x,y)
            Point2D.Double center = xyToPoint(point[0], point[1]);
            marker.moveTo(center.getX() + 2.75, center.getY() - 5);
            marker.lineTo(marker.getCurrentPoint().getX() - 5.5, marker.getCurrentPoint().getY());
            marker.moveTo(marker.getCurrentPoint().getX(), marker.getCurrentPoint().getY() + 10);
            marker.lineTo(marker.getCurrentPoint().getX() + 5.5, marker.getCurrentPoint().getY());
            marker.moveTo(center.getX(), marker.getCurrentPoint().getY());
            marker.lineTo(marker.getCurrentPoint().getX(), marker.getCurrentPoint().getY() - 10);
            marker.moveTo(center.getX() - 5, center.getY() + 2.75);
            marker.lineTo(marker.getCurrentPoint().getX(), marker.getCurrentPoint().getY() - 5.5);
            marker.moveTo(marker.getCurrentPoint().getX() + 10, marker.getCurrentPoint().getY());
            marker.lineTo(marker.getCurrentPoint().getX(), marker.getCurrentPoint().getY() + 5.5);
            marker.moveTo(marker.getCurrentPoint().getX(), center.getY());
            marker.lineTo(marker.getCurrentPoint().getX() - 10, marker.getCurrentPoint().getY());
            canvas.draw(marker);


        }
    }

    // Реализация отображения сетки
    public void paintCoordinateGrid(Graphics2D canvas){
        // Определение цены деления по оси Y
        double lengthY = maxY-minY;
        final double divisionValueY=findDivisionValue(lengthY);
        // Определение цены деления по оси X
        double lengthX = maxX-minX;
        final double divisionValueX=findDivisionValue(lengthX);
        // Нахождение длины в пикселях для шаблона линии
        double lengthLineAlongXInPixel=divisionValueX*scale;
        double lengthLineAlongYInPixel=divisionValueY*scale;
        // Нахождение цены одного деления линни в шаблоне в пикселях
        float x1=(float)lengthLineAlongXInPixel/20;
        float y1=(float)lengthLineAlongYInPixel/20;
        // Задания линии исходя из наших параметров
        coordinateGridStrokeAlongX = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,10.0f,
                new float[]{x1,x1,x1,x1,x1,x1,x1,x1,3*x1,x1,x1,x1,x1,x1,x1,x1,x1,x1},0.0f);
        coordinateGridStrokeAlongY = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,10.0f,
                new float[]{y1,y1,y1,y1,y1,y1,y1,y1,3*y1,y1,y1,y1,y1,y1,y1,y1,y1,y1},0.0f);
        // Установить особое начертания для линией вдольX
        canvas.setColor(Color.GRAY);
        canvas.setFont(gridFont);
        // Создать объект контекста отображения текста - для получения
        // характеристик устройства (экрана)
        FontRenderContext context = canvas.getFontRenderContext();
        // Нарисуем линии координатной сетки вдоль X
        if(maxX>0 && minX<0&&maxY>0&&minY<0){
            //Нарисуем координатные линии вдоль X
            canvas.setStroke(coordinateGridStrokeAlongX);
            Double currentCoordinateY = divisionValueY;
            while(currentCoordinateY<maxY){
                canvas.draw(new Line2D.Double(xyToPoint(0,currentCoordinateY),xyToPoint(minX,currentCoordinateY)));
                canvas.draw(new Line2D.Double(xyToPoint(0,currentCoordinateY),xyToPoint(maxX,currentCoordinateY)));
                Rectangle2D bounds = gridFont.getStringBounds(stringNumberWithoutTrash(currentCoordinateY),context);
                Point2D.Double labelPos = xyToPoint(0,currentCoordinateY);
                // Вывести надписи в точке с вычисленными координатами
                canvas.drawString(stringNumberWithoutTrash(currentCoordinateY),(float)labelPos.getX()+10,
                        (float)(labelPos.getY()-bounds.getY()));
                currentCoordinateY+=divisionValueY;
            }
            currentCoordinateY = -divisionValueY;
            while(currentCoordinateY>minY){
                canvas.draw(new Line2D.Double(xyToPoint(0,currentCoordinateY),xyToPoint(minX,currentCoordinateY)));
                canvas.draw(new Line2D.Double(xyToPoint(0,currentCoordinateY),xyToPoint(maxX,currentCoordinateY)));
                Rectangle2D bounds = gridFont.getStringBounds(stringNumberWithoutTrash(currentCoordinateY),context);
                Point2D.Double labelPos = xyToPoint(0,currentCoordinateY);
                canvas.drawString(stringNumberWithoutTrash(currentCoordinateY),(float)labelPos.getX()+10,
                        (float)(labelPos.getY()+bounds.getY()));
                currentCoordinateY-=divisionValueY;
            }
            //Нарисуем координатнуые линни вдоль Y
            canvas.setStroke(coordinateGridStrokeAlongY);
            Double currentCoordinateX = divisionValueX;
            while(currentCoordinateX<maxX){
                canvas.draw(new Line2D.Double(xyToPoint(currentCoordinateX,0),xyToPoint(currentCoordinateX,maxY)));
                canvas.draw(new Line2D.Double(xyToPoint(currentCoordinateX,0),xyToPoint(currentCoordinateX,minY)));
                Rectangle2D bounds = gridFont.getStringBounds(stringNumberWithoutTrash(currentCoordinateX),context);
                Point2D.Double labelPos = xyToPoint(currentCoordinateX,0);
                canvas.drawString(stringNumberWithoutTrash(currentCoordinateX),
                        (float)(labelPos.getX()-bounds.getX()-25),(float)labelPos.getY()-10);
                currentCoordinateX+=divisionValueX;

            }
            currentCoordinateX = -divisionValueX;
            while(currentCoordinateX>minX){
                canvas.draw(new Line2D.Double(xyToPoint(currentCoordinateX,0),xyToPoint(currentCoordinateX,maxY)));
                canvas.draw(new Line2D.Double(xyToPoint(currentCoordinateX,0),xyToPoint(currentCoordinateX,minY)));
                Rectangle2D bounds = gridFont.getStringBounds(stringNumberWithoutTrash(currentCoordinateX),context);
                Point2D.Double labelPos = xyToPoint(currentCoordinateX,0);
                canvas.drawString(stringNumberWithoutTrash(currentCoordinateX),(float)(labelPos.getX()+bounds.getX()),
                        (float)labelPos.getY()-10);
                currentCoordinateX-=divisionValueX;
            }
        }

    }

    // Метод помощник для строкового представления числа без мусора
    private String stringNumberWithoutTrash(Double number){

        String num = number.toString();
        if(num.length()>10) {
            String returnNum = "";
            int i = 0;
            while (num.charAt(i) != '.') {
                returnNum += num.charAt(i);
                i++;
            }
            returnNum += num.charAt(i);
            i++;
            for (int k = i; k <= i + numberOfDecimalPlaces; k++) {
                returnNum += num.charAt(k);
            }
            return returnNum;
        }else return num;
    }
    // Метод-помощник для
    private double findDivisionValue(double length){
        Double del = length/14;
        if(del>=1){
            Integer delInt=del.intValue();
            String delS=delInt.toString();
            return (delS.charAt(0)-'0')*Math.pow(10,delS.length()-1);
        }else{
            String delS=del.toString();
            int i=2;
            while(delS.charAt(i)=='0'){
                i++;
            }
            if(numberOfDecimalPlaces<i-1) {
                numberOfDecimalPlaces = i - 1;
            }
            return (delS.charAt(i)-'0')*Math.pow(10,-(i-1));
        }
    }
    // Метод-помощник для написания числа без мусора
    //Реализация метода перерисовки компонента paintComponent()
    public void paintComponent(Graphics g){
        /* Шаг 1 - Вызвать метод прдека для заливки области цветом заднего фона
        Эта функциональность - единственное, что осталось в наследство от
        paintComponent класса JPanel
         */
        super.paintComponent(g);
        // Шаг 2 - Если данные графика не загруженны (при показе компонента
        // при запуске программы) - ничего не делать
        if (graphicsData==null || graphicsData.length==0) return;
        // Шаг 3 - Определить начальные границы области отображения
        // Её верхнмй левый угол - (minX,maxY), правый нижний - (maxX,minY)
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length-1][0];
        minY = graphicsData[0][1];
        maxY = minY;
        // Найти минимальное и максимальное значение функции
        for(int i = 1;i<graphicsData.length;i++){
            if(graphicsData[i][1]<minY){
                minY = graphicsData[i][1];
            }
            if(graphicsData[i][1]>maxY){
                maxY = graphicsData[i][1];
            }
        }
        /* Шаг 4 - Определить (исходя из размеров окна) масштабы по осям X и Y -
        сколько пикселов приходиться на единицу длины по X и по Y*/
        double scaleX = getSize().getWidth()/(maxX-minX);
        double scaleY = getSize().getHeight()/(maxY-minY);
        // Выбрать единый масштаб как минимальный из двух
        scale = Math.min(scaleX,scaleY);
        // Шаг 5 - корректировка границ области согласно выбранному масштабу
        if(scale==scaleX){
            /* Если за основу был взят масштаб по оси X, значит по оси Y
            делений меньше, т.е. подлежащий отображению диапазон по Y будет меньше
            высоты окна. Значит необходимо добавить делений, сделаем это так:
            1) Вычислим, сколько делений влезет по Y при выбранном масштабе -
                getSize().getHeight()/scale;
            2) Вычтем из этого значения сколько делений требовалось изначально;
            3) Набросим по половине недостающего расстояния на maxY и minY */
            double yIncrement = (getSize().getHeight()/scale-(maxY-minY))/2;
            maxY+=yIncrement;
            minY-=yIncrement;
        }
        if(scale==scaleY){
            // Если за основу был взят масштаб по оси Y
            double xIncrement = (getSize().getWidth()/scale-(maxX-minX))/2;
            maxX+=xIncrement;
            minX-=xIncrement;
        }
        // Шаг 6 - Преобразовать экземпляр Graphics к Graphics2D
        Graphics2D canvas = (Graphics2D)g;
        // Шаг 7 - Сохранить текущие настройки холста
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();
        /* Шаг 8 - В нужном порядке вызвать методы отображения элементов графика
        Порядок вызова методоав имеет значение, т.к. предыдущий рисунок будет
        затираться последубщим
        Первым (если нужно) отрисовываються оси координат.
         */
        if(showAxis) paintAxis(canvas);
        // Затем отображаеться сам график
        paintGraphics(canvas);
        // Затем (если нужно) отображаються маркеры точек графика.
        if(showMarkers) paintMarkers(canvas);
        if(showCoordinateGrid) paintCoordinateGrid(canvas);
        // Шаг 9 - Восстановить старые настройки холста
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }



}
