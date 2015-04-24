package com.ikueb.calculeightor;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

public class CalculeightorGUI extends JFrame implements Calculeightor<Double> {

    private static final long serialVersionUID = -1L;

    private final Collection<JButton> numbers = new HashSet<>();
    private final Collection<JButton> operators = new HashSet<>();
    private final JButton equalsOp = equalsButton();
    private final JLabel label = new JLabel();
    private final List<Double> inputs = new ArrayList<>(2);
    private BinaryOperator<Double> operator;
    private State state = null;

    private enum State {
        INIT {
            @Override
            void set(CalculeightorGUI instance) {
                toggle(instance.operators.stream(), Stream.of(instance.equalsOp));
                instance.label.setHorizontalAlignment(SwingConstants.CENTER);
            }
        },
        FIRST_OPERAND {
            @Override
            void set(CalculeightorGUI instance) {
                toggle(instance.numbers.stream(), instance.operators.stream());
            }
        },
        OPERATOR {
            @Override
            void set(CalculeightorGUI instance) {
                toggle(instance.numbers.stream(), instance.operators.stream());
            }
        },
        SECOND_OPERAND {
            @Override
            void set(CalculeightorGUI instance) {
                toggle(instance.numbers.stream(), Stream.of(instance.equalsOp));
            }
        },
        EQUALS {
            @Override
            void set(CalculeightorGUI instance) {
                toggle(instance.numbers.stream(), Stream.of(instance.equalsOp));
                instance.inputs.clear();
            }
        };

        State next() {
            return this == EQUALS ? FIRST_OPERAND : values()[ordinal() + 1];
        }

        abstract void set(CalculeightorGUI instance);

        private static <T extends Component> void toggle(Stream<T>... components) {
            Stream.of(components).flatMap(Function.identity())
                    .forEach(c -> c.setEnabled(!c.isEnabled()));
        }

    }

    public CalculeightorGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(getClass().getSimpleName());
        getContentPane().setLayout(new GridLayout(0, 1));
        Stream.of(numbersPanel(), operatorsPanel(), panelOf(Stream.of(equalsOp, label)))
                .forEach(this::add);
        updateState();
        pack();
        setVisible(true);
    }

    private void updateState() {
        state = state == null ? State.INIT : state.next();
        state.set(this);
    }

    private JPanel numbersPanel() {
        return panelOf(IntStream.rangeClosed(0, 9).boxed(), numbers, event -> {
            Double value = Double.valueOf((((JButton) event.getSource()).getText()));
            appendValue(value);
            label.setText(Integer.toString(value.intValue()));
            updateState();
        });
    }

    private JPanel operatorsPanel() {
        return panelOf(Stream.of(Operator.values()), operators, event -> {
            String value = ((JButton) event.getSource()).getText();
            setOperator(Operator.of(value));
            label.setText(value);
            updateState();
        });
    }

    private <T> JPanel panelOf(Stream<T> values, Collection<JButton> collection,
            ActionListener listener) {
        return panelOf(values.map(v -> createButton(v, listener)).peek(collection::add));
    }

    private static <T extends Component> JPanel panelOf(Stream<T> components) {
        JPanel panel = new JPanel(new GridLayout());
        components.forEach(panel::add);
        return panel;
    }

    private JButton equalsButton() {
        return createButton("=", event -> {
            Double value = getResult();
            label.setText(value.doubleValue() % 1 == 0 ? Integer.toString(value
                    .intValue()) : value.toString());
            label.setForeground(value.isInfinite() ? Color.RED : Color.BLACK);
            updateState();
        });
    }

    private static JButton createButton(Object component, ActionListener listener) {
        String text = component.toString();
        JButton button = new JButton(text);
        button.addActionListener(listener);
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(text.charAt(0)), text);
        button.getActionMap().put(text, getAction(button));
        return button;
    }

    @SuppressWarnings("serial")
    private static Action getAction(JButton button) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.doClick();
            }
        };
    }

    @Override
    public void appendValue(Double value) {
        inputs.add(value);
    }

    @Override
    public Stream<Double> getValues() {
        return inputs.stream();
    }

    @Override
    public void setOperator(BinaryOperator<Double> operator) {
        this.operator = operator;
    }

    @Override
    public BinaryOperator<Double> getOperator() {
        return operator;
    }

    public static void main(String[] args) {
        new CalculeightorGUI();
    }
}
