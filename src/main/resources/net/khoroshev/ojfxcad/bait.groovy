public class Bait{
    private double high = 20D;
    private double width = 15D;
    private double topRound = 7D/8D;
    private double bottomRound = 3D/4D;
    private double tailLengh = 35D;
    private double headLengh = 35D;
    private int numSlices = 100;

    private CSG section(double high, double width){
        double halfHigh=high/2;
        double halfWidth=width/2;
        double topFilletR=width*topRound/2;
        double bottomFilletR=width*bottomRound/2;
        CSG bottomCylinder = (new Cylinder(bottomFilletR, 0.01D, numSlices)).toCSG()
                .transformed(Transform.unity().translate(0D, -halfHigh + bottomFilletR, 0));
        CSG rightTopCylinder = (new Cylinder(topFilletR, 0.01D, numSlices)).toCSG()
                .transformed(Transform.unity().translate(halfWidth - topFilletR, halfHigh - topFilletR, 0));
        CSG leftTopCylinder = (new Cylinder(topFilletR, 0.01D, numSlices)).toCSG()
                .transformed(Transform.unity().translate(-halfWidth + topFilletR, halfHigh - topFilletR, 0));
        return bottomCylinder.hull(rightTopCylinder, leftTopCylinder);
    }

    private CSG tail() {
        List<CSG> sections = new ArrayList<>();
        for (int i = 1; i <= tailLengh; i++) {
            double coef = Math.sqrt(new Double(i)/tailLengh);
            sections.add(
                    section(high*coef,width*coef).transformed(Transform.unity().translate(0, 0, i))
            );
        }
        return sections.get(0).hull(sections)
                .transformed(Transform.unity().mirror(eu.mihosoft.vvecmath.Plane.XZ_PLANE))
                .transformed(Transform.unity().translate(0,0,tailLengh));
    }

    private CSG head() {
        double sphereD = Math.min(Math.min(high, width), headLengh)/2;
        double sphereR = sphereD/2;
        List<CSG> sections = new ArrayList<>();
        for (int i = 1; i <= headLengh; i++) {
            if (i >= sphereD) {
                double coef = Math.sqrt(Math.sqrt(new Double(i)/headLengh));
                sections.add(
                        section(high*coef,width*coef).transformed(Transform.unity().translate(0, 0, i))
                );
            }
        }
        return new Sphere(sphereR, numSlices, numSlices).toCSG()
                .transformed(Transform.unity().translate(0, 0, sphereR))
                .hull(sections)
                .transformed(Transform.unity().mirror(eu.mihosoft.vvecmath.Plane.XZ_PLANE))
                .transformed(Transform.unity().translate(0,0,tailLengh));
    }
    public CSG toCSG() {
        return head();
        //return tail()
        //return tail().hull(head());
    }
}

(new Bait()).toCSG()