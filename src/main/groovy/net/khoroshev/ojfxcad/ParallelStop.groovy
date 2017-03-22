package net.khoroshev.ojfxcad

import eu.mihosoft.jcsg.CSG
import static eu.mihosoft.vvecmath.Transform.unity

import static net.khoroshev.ojfxcad.Plywood.Thickness.*
import static eu.mihosoft.vvecmath.Plane.*
/**
 * Параллельный упор для стола с циркуляркой
 */
public class ParallelStop implements CSGProducer {
    private static double width = 7.0D;
    private static double height = 10.0D
    private static Plywood.Thickness th = T12;
    //Ширина стола
    double tableWidth
    //общая длина этой конструкции
    double thisLength

    ParallelStop(double tableWidth) {
        this.tableWidth = tableWidth
        this.thisLength = tableWidth + NearPart.height*2
    }

    private class NearPart implements CSGProducer{
        public static double height = 5.0D;
        public static double width = 30.0D;
        @Override
        CSG toCSG() {
            return new Plywood(height, width, th, XY_PLANE).toCSG()
        }
    }

    private class BaseBoard implements CSGProducer {
        public double width = ParallelStop.this.tableWidth + NearPart.height*2
        public static double height = ParallelStop.width - th.value*2
        @Override
        CSG toCSG() {
            return new Plywood(width, height, th, XY_PLANE).toCSG()
        }
    }

    private class Brake implements CSGProducer {
        public static double width = ParallelStop.width - th.value*2

        @Override
        CSG toCSG() {
            return new Plywood(15, width, th, XY_PLANE).toCSG()
                    .transformed(unity().translateZ(th.value))
                    .union(
                    new Plywood(5, width, th, XY_PLANE).toCSG()
                            .transformed(unity().translateX(-15/2 + 5/2)),
                    new Plywood(10, width, th, XY_PLANE).toCSG()
                            .transformed(unity().translateX(-15/2 + 10/2))
                            .transformed(unity().translateZ(-th.value))
            )
        }
    }

    private class PlaneBoard implements CSGProducer {
        @Override
        CSG toCSG() {
            return new Plywood(ParallelStop.this.thisLength, height, th, XY_PLANE).toCSG()
        }
    }

    @Override
    CSG toCSG() {
        BaseBoard baseBoard = new BaseBoard()
        NearPart nearPart = new NearPart()
        PlaneBoard planeBoard = new PlaneBoard();
        Brake brake = new Brake()
        return baseBoard.toCSG().union(
                //Направляющая
                nearPart.toCSG().transformed(unity().translate(this.thisLength/2 - NearPart.height/2, 0, -th.value)),
                //Левая плоскость
                planeBoard.toCSG()
                    .transformed(unity().rotX(90))
                    .transformed(unity().translate(0, width/2 - th.value/2, height/2 - th.value/2)),
                //Правая плоскость
                planeBoard.toCSG()
                        .transformed(unity().rotX(90))
                        .transformed(unity().translate(0, -width/2 + th.value/2, height/2 - th.value/2)),
                //Тормоз
                brake.toCSG()
                    .transformed(unity().translateX(-thisLength/2))
        )
    }
}

