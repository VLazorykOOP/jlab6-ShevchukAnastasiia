import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrbitGUI {

    static class Planet {
        double x, y, radius;

        public Planet(double x, double y, double radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
    }

    static class Satellite {
        double x, y;
        double semiMajorAxis, semiMinorAxis, angle;
        Planet planet;

        public Satellite(Planet planet, double semiMajorAxis, double semiMinorAxis) {
            this.planet = planet;
            this.semiMajorAxis = semiMajorAxis;
            this.semiMinorAxis = semiMinorAxis;
            this.angle = 0;
            updatePosition(0);
        }

        public void updatePosition(double angleIncrement) {
            this.angle += angleIncrement;
            if (this.angle >= (2 * Math.PI)) {
                this.angle -= (2 * Math.PI);
            }

            this.x = planet.x + this.semiMajorAxis * Math.cos(this.angle);
            this.y = planet.y + this.semiMinorAxis * Math.sin(this.angle);
        }


        public boolean isOcculted() {
            boolean isBehindY = this.y > planet.y; // 'y' супутника більший за 'y' планети
            boolean isWithinShadowX = (this.x > (planet.x - planet.radius)) &&
                    (this.x < (planet.x + planet.radius));
            return isBehindY && isWithinShadowX;
        }
    }


    static class OrbitPanel extends JPanel {
        Planet planet;
        Satellite satellite;

        public OrbitPanel(Planet p, Satellite s) {
            this.planet = p;
            this.satellite = s;
            // Встановлюємо білий фон для нашого "космосу"
            this.setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;


            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);


            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            g2d.translate(centerX, centerY);


            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawOval(
                    (int)(planet.x - satellite.semiMajorAxis),
                    (int)(planet.y - satellite.semiMinorAxis), // Ми використовуємо '-y' для інверсії осі
                    (int)(2 * satellite.semiMajorAxis),
                    (int)(2 * satellite.semiMinorAxis)
            );


            g2d.setColor(Color.BLUE);
            int r = (int)planet.radius;

            g2d.fillOval((int)planet.x - r, (int)planet.y - r, 2 * r, 2 * r);


            boolean isHidden = satellite.isOcculted();
            if (isHidden) {
                g2d.setColor(Color.DARK_GRAY); // Сірий, якщо схований
            } else {
                g2d.setColor(Color.RED); // Червоний, якщо видимий
            }

            int satSize = 10;

            g2d.fillOval((int)satellite.x - satSize/2,
                    (int)-satellite.y - satSize/2, // інверсія осі Y
                    satSize, satSize);


            String status = isHidden ? "СХОВАНИЙ" : "ВИДИМИЙ";
            g2d.setColor(Color.BLACK);

            g2d.drawString(status, -centerX + 10, -centerY + 20);
            g2d.drawString(String.format("X: %.0f", satellite.x), -centerX + 10, -centerY + 40);
            g2d.drawString(String.format("Y: %.0f", satellite.y), -centerX + 10, -centerY + 60);
        }
    }



    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                Planet earth = new Planet(0, 0, 50);
                Satellite sputnik = new Satellite(earth, 350, 200);


                JFrame frame = new JFrame("Симуляція орбіти супутника (Swing)");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600); // Розмір вікна
                frame.setLocationRelativeTo(null); // Вікно по центру


                OrbitPanel orbitPanel = new OrbitPanel(earth, sputnik);
                frame.add(orbitPanel);


                double angleStep = (2 * Math.PI) / 360;
                int delay = 20; // 20 мілісекунд ~ 50 кадрів/сек

                Timer timer = new Timer(delay, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {




                        sputnik.updatePosition(angleStep);


                        orbitPanel.repaint();
                    }
                });


                frame.setVisible(true); // Робимо вікно видимим
                timer.start(); // Запускаємо анімацію
            }
        });
    }
}