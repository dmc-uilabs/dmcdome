// Modes.java
package mit.cadlab.dome3.gui.mode;

import mit.cadlab.dome3.util.MultipleErrorsException;

import java.lang.reflect.Method;
import java.util.Vector;
import javax.swing.AbstractAction;

public class Modes
{
    public static final int SERVER = -1;

    public static final int BUILD_MODE = 0;
    public static final int DEPLOY_MODE = 1;
    public static final int RUN_MODE = 2;
    public static final int SERVER_MODE = 3; // on the client side
    public static final int API_MODE = 4;

	public static AbstractAction exitAction; // to be set by application

	protected static _Mode[] modes = {};

	public static void registerMode(String modeClassName, String modeMenusClassName)
	{
		if (exitAction == null)
			throw new NullPointerException("Modes.exitAction is null -- must be set in application");
		try {
			Class modeClass = Class.forName(modeClassName);
			_Mode newMode = new _Mode(modeClass);
			_Mode[] newModes = new _Mode[modes.length + 1];
			for (int i = 0; i < modes.length; ++i)
				newModes[i] = modes[i];
			int newModeId = modes.length;
			newModes[newModeId] = newMode;
			modes = newModes;
			initializeModeMenus(modeMenusClassName, newModeId);
		} catch (ClassNotFoundException ex) {
			System.out.println(ex);
		} catch (RuntimeException ex) {
			System.err.println("Error registering mode " + modeClassName);
			throw ex;
		}
	}

	public static int countModes()
	{
		return modes.length;
	}

	public static String getModeName(int modeId)
	{
		if (!isValidModeId(modeId)) return null;
		return modes[modeId].name;
	}

	public static void hideMode(int modeId)
	{
		if (!isValidModeId(modeId)) return;
		try {
			modes[modeId].hide.invoke(null, null);
		} catch (Exception ex) {
			throw new RuntimeException("error hiding mode " + modes[modeId].name);
		}
	}

	public static void showMode(int modeId)
	{
		if (!isValidModeId(modeId)) return;
		try {
			modes[modeId].show.invoke(null, null);
		} catch (Exception ex) {
			throw new RuntimeException("error showing mode " + modes[modeId].name);
		}
	}

	public static void exitMode(int modeId)
	{
		if (!isValidModeId(modeId)) return;
		try {
			modes[modeId].exit.invoke(null, null);
		} catch (Exception ex) {
			throw new RuntimeException("error exiting mode " + modes[modeId].name);
		}
	}

	public static void hideAllModes()
	{
		for (int i = 0; i < modes.length; ++i)
			hideMode(i);
	}

	public static void showAllModes()
	{
		for (int i = 0; i < modes.length; ++i)
			showMode(i);
	}

	public static void exitAllModes()
	{
		for (int i = 0; i < modes.length; ++i)
			exitMode(i);
	}

	public static boolean isValidModeId(int modeId)
	{
		return modeId >= 0 && modeId < modes.length;
	}

	protected static void initializeModeMenus(String modeMenusClassName, int modeId)
	{
		try {
			Class modeMenusClass = Class.forName(modeMenusClassName);
			Method initMethod = modeMenusClass.getMethod("initialize", new Class[]{int.class});
			initMethod.invoke(null, new Object[]{new Integer(modeId)});
		} catch (java.lang.reflect.InvocationTargetException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

	protected static class _Mode
	{
		public String name;
		public Method show;
		public Method hide;
		public Method exit;

		public _Mode(Class modeClass)
		{
			Vector errors = new Vector();
			try {
				Method method = modeClass.getMethod("name", null);
				name = (String) method.invoke(null, null);
				if (name == null)
					errors.add(new NullPointerException("mode name can not be null"));
			} catch (Exception ex) {
				errors.add(new NoSuchMethodException("mode does not support name method"));
			}
			try {
				show = modeClass.getMethod("show", null);
			} catch (Exception ex) {
				errors.add(new NoSuchMethodException("mode does not support show method"));
			}
			try {
				hide = modeClass.getMethod("hide", null);
			} catch (Exception ex) {
				errors.add(new NoSuchMethodException("mode does not support hide method"));
			}
			try {
				exit = modeClass.getMethod("exit", null);
			} catch (Exception ex) {
				errors.add(new NoSuchMethodException("mode does not support exit method"));
			}
			if (errors.size() > 0) {
				throw new MultipleErrorsException("Modes.registerMode errors for " + modeClass.getName(), errors);
			}
		}
	}

}
