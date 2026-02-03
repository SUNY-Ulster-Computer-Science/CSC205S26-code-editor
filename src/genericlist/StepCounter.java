package genericlist;

public class StepCounter {
private int steps = 0;
public void inc() { steps++; }
public void add(int n) { steps += n; }
public int get() { return steps; }
public void reset() { steps = 0; }
}