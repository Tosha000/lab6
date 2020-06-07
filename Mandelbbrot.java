package lab_6;

import java.awt.geom.Rectangle2D;

class Mandelbrot extends FractalGenerator {
    public String toString() {
        return "Mandelbrot";
    }

    public void getInitialRange(Rectangle2D.Double range) { // Double класс определяет диапазон (range) прямоугольника в координатах х и у
        range.x = -2; //x для Мандельброта
        range.y = -1.5; //y для Мандельброта
        range.width = 3; // Ширина для Мандельброта
        range.height = 3; // Высота для Мандельброта
    }

    public static final int MAX_ITERATIONS = 2000; //Константа с максимальным количеством итераций

    public int numIterations(double x, double y) { // Реализует итеративную функцию для фрактала Мандельброта (рассчитывает количество итераций для соответсвующей координаты
        int iteration = 0;
        double real = 0;
        double imaginary = 0;
        while ((iteration < MAX_ITERATIONS) && (real * real + imaginary * imaginary) < 4) {
            double realUpdated = real * real - imaginary * imaginary + x;
            double imaginaryUpdated = 2 * real * imaginary + y;
            real = realUpdated;
            imaginary = imaginaryUpdated;
            iteration++;
        }
        if (iteration == MAX_ITERATIONS) {
            return -1;
        }
        return iteration;
    }
}
