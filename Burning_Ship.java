package lab_6;

import java.awt.geom.Rectangle2D;

public class Burning_Ship extends FractalGenerator {
    public String toString(){
        return "Burning_Ship";
    }
    public void getInitialRange(Rectangle2D.Double range) { // Double класс определяет диапазон (range) прямоугольника в координатах х и у
        range.x=-2; //x для
        range.y=-2.5; //y для
        range.width=4; // Ширина
        range.height=4; // Высота
    }
    public static final int MAX_ITERATIONS = 2000; //Константа с максимальным количеством итераций
    public int numIterations(double x, double y) { // Реализует итеративную функцию для фрактала Мандельброта (рассчитывает количество итераций для соответсвующей координаты
        int iteration = 0;
        double real = 0;
        double imaginary = 0;
        while ((iteration < MAX_ITERATIONS) && (real * real + imaginary * imaginary) < 4) {
            double realUpdated = Math.abs(real * real - imaginary * imaginary + x);
            double imaginaryUpdated = Math.abs(2 * real * imaginary + y);
            real = realUpdated;
            imaginary = imaginaryUpdated;
            iteration += 1;
        }
        if (iteration == MAX_ITERATIONS)
        {
            return -1;
        }
        return iteration;
    }
}
