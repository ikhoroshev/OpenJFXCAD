package net.khoroshev.ojfxcad

import eu.mihosoft.jcsg.CSG
import static eu.mihosoft.vvecmath.Transform.unity

import static net.khoroshev.ojfxcad.Plywood.Thickness.*
import static eu.mihosoft.vvecmath.Plane.*
/**
 * Параллельный упор для стола с циркуляркой
 */
public class ParallelStop implements CSGProducer {
    private static double width = 10.0D;
    private static double height = 10.0D
    private static Plywood.Thickness th = T12;
    //Ширина стола
    double tableWidth

    ParallelStop(double tableWidth) {
        this.tableWidth = tableWidth
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
        public double width = ParallelStop.this.tableWidth + NearPart.height
        public static double height = ParallelStop.width - th.value*2
        @Override
        CSG toCSG() {
            return new Plywood(width, height, th, XY_PLANE).toCSG()
        }
    }

    private class PlaneBoard implements CSGProducer {
        @Override
        CSG toCSG() {
            return new Plywood(ParallelStop.this.tableWidth, height, th, XY_PLANE).toCSG()
        }
    }

    @Override
    CSG toCSG() {
        BaseBoard baseBoard = new BaseBoard()
        NearPart nearPart = new NearPart()
        PlaneBoard planeBoard = new PlaneBoard();
        return baseBoard.toCSG().union(
                //Направляющая
                nearPart.toCSG().transformed(unity().translate(baseBoard.width/2 - NearPart.height/2, 0, -th.value)),
                //Левая плоскость
                planeBoard.toCSG()
                    .transformed(unity().rotX(90))
                    .transformed(unity().translate(-NearPart.height, baseBoard.height/2, height/2))
        )
    }
}

