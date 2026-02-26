package genericlist;

/*
 * allows for protected adjustment of list size
 */
public class StepCounter {

/*represents amount of steps*/
private int steps = 0;

/*
 * increases # of steps by 1
 * @param none
 * @return none
 */
public void inc() { steps++; }

/*
 * increases # of steps by n
 * @param int n - number of added steps
 */
public void add(int n) { steps += n; }

/*
 * retuns # of steps
 * @param none
 * @return steps
 */
public int get() { return steps; }

/*
 * sets steps to 0
 * @param none
 * @return none
 */
public void reset() { steps = 0; }
}