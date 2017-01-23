package jacob.threads;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 28, 2004
 * Time: 6:46:03 PM
 * To change this template use Options | File Templates.
 */
public class SimpleThread implements Runnable
{
    private String _threadId;

    public SimpleThread(String threadId)
    {
        _threadId = threadId;
    }

    public void run()
    {
        for (int i=0; i < 1000; i++)
            System.out.println(_threadId + " says hello!");
    }
}
