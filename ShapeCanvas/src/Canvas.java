//Tyler Nguyen - Canvas Project - MW Section 3

import java.awt.*;
import java.awt.event.*;
import java.awt.Graphics;
import javax.swing.*;
import java.util.*;

//The drawing panel that graphics will be added to
class drawPanel extends JPanel {
    Shape shapes;

    public drawPanel() {
        super();
    }

    public void setShapes(Shape shapes) {
        this.shapes = shapes;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (shapes != null)
            shapes.draw(g);
    }
}

// GUI
class frame extends JFrame {
    JFrame frame = new JFrame("Canvas");
    JButton square_Button = new JButton();
    JButton circle_Button = new JButton();
    JButton hello_Button = new JButton();
    JButton undo_Button = new JButton();
    JButton redo_Button = new JButton();
    drawPanel drawingPanel = new drawPanel();
    JPanel buttonsPanel = new JPanel();
    private Controller con;

    frame() {
        con = new Controller();
        square_Button.setText("Square");
        circle_Button.setText("Circle");
        hello_Button.setText("Hello World");
        undo_Button.setText("Undo");
        redo_Button.setText("Redo");

        square_Button.setBounds(0, 0, 100, 20);
        circle_Button.setBounds(0, 20, 100, 20);
        hello_Button.setBounds(0, 40, 100, 20);
        undo_Button.setBounds(0, 60, 100, 20);
        redo_Button.setBounds(0, 80, 100, 20);

        buttonsPanel.setLayout(null);
        buttonsPanel.setBounds(0, 0, 120, 100);

        buttonsPanel.add(square_Button);
        buttonsPanel.add(circle_Button);
        buttonsPanel.add(hello_Button);
        buttonsPanel.add(undo_Button);
        buttonsPanel.add(redo_Button);

        drawingPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                con.drawPanelClick((int) e.getPoint().getX(), (int) e.getPoint().getY());
                drawingPanel.shapes = con.getShapes();
                drawingPanel.repaint();
            }
        });

        square_Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                con.squareClick();
            }
        });

        circle_Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                con.circleClick();
            }
        });

        hello_Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                con.helloClick();
            }
        });

        undo_Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                con.undoClick();
                drawingPanel.shapes = con.getShapes();
                drawingPanel.repaint();
            }
        });

        redo_Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                con.redoClick();
                drawingPanel.shapes = con.getShapes();
                drawingPanel.repaint();
            }
        });

        drawingPanel.setLayout(null);
        drawingPanel.setBounds(100, 0, 510, 400);

        drawingPanel.setBackground(new java.awt.Color(255, 255, 255));
        drawingPanel.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        frame.setLayout(null);
        frame.add(drawingPanel);
        frame.add(buttonsPanel);
        frame.setSize(600, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Controller getController() {
        return con;
    }
}

/* Controller */
class Controller {
    private int curr_Shape = 0; // 1 = square, 2 = circle, 3 = hello world
    Shape_Comp shapes;
    Command comm;
    Shape_State_Context state; // State pattern implementation

    Controller() {
        shapes = new Shape_Comp();
        state = new Shape_State_Context();
    }

    public Shape getShapes() {
        return shapes;
    }

    public void squareClick() {
        // curr_Shape = 1;
        state.set_State(new Square_State(comm, shapes));
    }

    public void circleClick() {
        // curr_Shape = 2;
        state.set_State(new Circle_State(comm, shapes));
    }

    public void helloClick() {
        // curr_Shape = 3;
        state.set_State(new Hello_State(comm, shapes));
    }

    public void undoClick() {
        if (!shapes.coll.isEmpty()) {
            shapes.redo.push(shapes.coll.pop());
        }
    }

    public void redoClick() {
        if (!shapes.redo.isEmpty()) {
            shapes.coll.push(shapes.redo.pop());
        }
    }

    public void drawPanelClick(int x, int y) {
        shapes.redo.clear();
        state.execute(x, y);
        state.set_State(new Default_State()); // goes back to initial state where nothing happens on cnavas click
        /*
         * switch(curr_Shape) { case 0 : return; case 1 : comm = new Add_SQ(shapes, x,
         * y); comm.execute(); curr_Shape = 0; return; case 2: comm = new
         * Add_CIR(shapes, x, y); comm.execute(); curr_Shape = 0; return; case 3: comm =
         * new Add_HEL(shapes, x, y); comm.execute(); curr_Shape = 0; return; }
         */
    }
}

// State Pattern
interface Current_Shape_State {
    public void execute(int x, int y);
}

class Square_State implements Current_Shape_State {
    Command comm;
    Shape_Comp shapes;

    public Square_State(Command comm, Shape_Comp shapes) {
        this.comm = comm;
        this.shapes = shapes;
    }

    public void execute(int x, int y) {
        comm = new Add_SQ(shapes, x, y);
        comm.execute();
    }
}

class Circle_State implements Current_Shape_State {
    Command comm;
    Shape_Comp shapes;

    public Circle_State(Command comm, Shape_Comp shapes) {
        this.comm = comm;
        this.shapes = shapes;
    }

    public void execute(int x, int y) {
        comm = new Add_CIR(shapes, x, y);
        comm.execute();
    }
}

class Hello_State implements Current_Shape_State {
    Command comm;
    Shape_Comp shapes;

    public Hello_State(Command comm, Shape_Comp shapes) {
        this.comm = comm;
        this.shapes = shapes;

    }

    public void execute(int x, int y) {
        comm = new Add_HEL(shapes, x, y);
        comm.execute();
    }
}

class Default_State implements Current_Shape_State { // does nothing
    public Default_State() {

    }

    public void execute(int x, int y) {

    }
}

class Shape_State_Context {
    Current_Shape_State state;

    public Shape_State_Context() {
        state = new Default_State();
    }

    public void set_State(Current_Shape_State new_State) {
        state = new_State;
    }

    public void execute(int x, int y) {
        state.execute(x, y);
    }
}

// Composite
abstract class Shape {
    int x, y;

    public Shape(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void draw(Graphics g);

    public void add(Shape s) {

    }
}

// Composite
class Shape_Comp extends Shape {
    Stack<Shape> coll;
    Stack<Shape> redo;

    public Shape_Comp() {
        super(0, 0);
        coll = new Stack<Shape>();
        redo = new Stack<Shape>();
    }

    public void add(Shape shape) {
        coll.push(shape);
    }

    public void draw(Graphics g) {
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            ((Shape) it.next()).draw(g);
        }
    }
}

class Hello extends Shape {
    public Hello(int x, int y) {
        super(x, y);
    }

    public void draw(Graphics g) {
        g.drawString("Hello World", x, y);
    }
}

class Square extends Shape {
    public Square(int x, int y) {
        super(x, y);
    }

    public void draw(Graphics g) {
        g.drawRect(this.x, this.y, 20, 20);
    }
}

class Circle extends Shape {
    public Circle(int x, int y) {
        super(x, y);
    }

    public void draw(Graphics g) {
        g.drawOval(this.x, this.y, 20, 20);
    }
}

// Command Interface
interface Command {
    public void execute();
}

// Concrete commands
class Add_SQ implements Command {
    Shape_Comp shapes;
    int x, y;

    public Add_SQ(Shape_Comp shapes, int x, int y) {
        this.shapes = shapes;
        this.x = x;
        this.y = y;
    }

    public void execute() {
        Square s = new Square(x, y);
        shapes.add(s);
    }
}

class Add_CIR implements Command {
    Shape_Comp shapes;
    int x, y;

    public Add_CIR(Shape_Comp shapes, int x, int y) {
        this.shapes = shapes;
        this.x = x;
        this.y = y;
    }

    public void execute() {
        Circle s = new Circle(x, y);
        shapes.add(s);
    }
}

class Add_HEL implements Command {
    Shape_Comp shapes;
    int x, y;

    public Add_HEL(Shape_Comp shapes, int x, int y) {
        this.shapes = shapes;
        this.x = x;
        this.y = y;
    }

    public void execute() {
        Hello s = new Hello(x, y);
        shapes.add(s);
    }
}

public class Canvas {
    public static void main(String[] args) {
        frame f = new frame();
    }
}