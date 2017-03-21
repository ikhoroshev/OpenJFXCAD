package net.khoroshev.ojfxcad

import eu.mihosoft.jcsg.CSG
import eu.mihosoft.jcsg.Cube
import eu.mihosoft.vvecmath.Plane

import static eu.mihosoft.vvecmath.Transform.unity

/**
 * Created by sbt-khoroshev-iv on 21.03.2017.
 */
class Plywood {
    double width
    double height
    Plane place
    Thickness thickness
    public static enum Thickness {
        T4(0.4D),
        T6(0.6D),
        T8(0.8D),
        T12(1.2D),
        T15(1.5D),
        T16(1.6D)

        double value;
        Thickness(double value) {
            this.value = value
        }
    }

    Plywood(double width, double height, Thickness thickness, Plane place) {
        this.width = width
        this.height = height
        this.thickness = thickness;
        this.place = place
    }

    public CSG toCSG() {
        def result = new Cube(width, height, thickness.value).toCSG()
        if (place != null && place != Plane.XY_PLANE) {
            result = result.transformed(place == Plane.XZ_PLANE ? unity().rotX(90) : unity().rotY(90))
        }
        return result
    }
}
