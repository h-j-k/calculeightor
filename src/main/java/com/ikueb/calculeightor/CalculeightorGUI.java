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

/**
 * The GUI implementation of {@link Calculeightor}.
 * <p>
 * The input sequence is enforced by enabling different buttons at each stage. The
 * stages are:
 * <ol>
 * <li>Get a number</li>
 * <li>Select an operator</li>
 * <li>Get the next number</li>
 * <li>Press the {@code "="} button to compute the answer</li>
 * </ol>
 * Results are displayed as integer values if possible.
 */
public class CalculeightorGUI extends JFrame implements Calculeightor<Double> {

    private static final long serialVersionUID = -1L;

    private final Collection<JButton> numbers = new HashSet<>();
    private final Collection<JButton> operators = new HashSet<>();
    private final JButton equalsOp = equalsButton();
    private final JLabel label = new JLabel();
    private final List<Double> inputs = new ArrayList<>(2);
    private BinaryOperator<Double> operator;
    private State state;

    /**
     * Controls what {@link Component}s need to be configured at different stages of
     * receiving input by calling {@link State#set(CalculeightorGUI)}. As such, the
     * values represent what <em>has already</em> happened, and <em>has been</em> set.
     */
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

    /**
     * Updates the latest state and also the input configuration.
     */
    private void updateState() {
        (state = state == null ? State.INIT : state.next()).set(this);
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

    /**
     * Creates {@link JButton}s representing each stream's elements.
     *
     * @param values
     * @param collection the collection to add the created {@link JButton}s to
     * @param listener the listener to receive events from the {@link JButton}s
     * @return a single-row {@link JPanel} containing the created {@link JButton}s
     */
    private <T> JPanel panelOf(Stream<T> values, Collection<JButton> collection,
            ActionListener listener) {
        return panelOf(values.map(v -> createButton(v, listener)).peek(collection::add));
    }

    /**
     * @param components
     * @return a single-row {@link JPanel} containing the {@link Component}s
     */
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

    /**
     * @param input the input to use
     * @param listener the listener to receive events from the {@link JButton}
     * @return a {@link JButton} with the input as its text and a key binding of the
     *         first character while the window is in focus
     */
    private static JButton createButton(Object input, ActionListener listener) {
        String text = input.toString();
        JButton button = new JButton(text);
        button.addActionListener(listener);
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(text.charAt(0)), text);
        button.getActionMap().put(text, getAction(button));
        return button;
    }

    /**
     * @param button the {@link JButton} to configure for
     * @return an {@link Action} that clicks the button
     */
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
