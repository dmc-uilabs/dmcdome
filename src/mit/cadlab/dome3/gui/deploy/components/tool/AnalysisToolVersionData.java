package mit.cadlab.dome3.gui.deploy.components.tool;

import mit.cadlab.dome3.network.client.connection.ServerConnection;
import mit.cadlab.dome3.gui.deploy.components.InterfaceVersionData;

import java.util.*;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Jacob A. Wronski
 * User: jacobwronski
 * Date: Jan 10, 2004
 * Time: 7:01:39 PM
 * To change this template use Options | File Templates.
 */
public class AnalysisToolVersionData
{
    private ServerConnection _svrConn;
    private String _svrPort, _user, _analysisToolId;
    private Integer _version;

    private HashMap _analysisToolInterfacesByBuildId = new HashMap();
    private HashMap _analysisToolInterfacesByInterfaceId = new HashMap();

    public AnalysisToolVersionData(String analysisToolVersionFile)
    {
        try
        {
            FileReader fileReader = new FileReader(analysisToolVersionFile);
            BufferedReader bufferReader = new BufferedReader(fileReader);
            String aLine = bufferReader.readLine();
            String[] firstLineInFile = aLine.split("\t");

            _svrPort = firstLineInFile[0];
            _user = firstLineInFile[1];

            String[] analysisToolInformation = bufferReader.readLine().split("\t");
            _analysisToolId = analysisToolInformation[1];
            _version = new Integer(analysisToolInformation[2]);


            //Load the analysis tool Interfaces
            InterfaceVersionData ivd;
            String[] interfaceInformation;
            int nbInterfaces = new Integer(analysisToolInformation[3]).intValue();
            for (int i = 0; i < nbInterfaces; i++)
            {
                aLine = bufferReader.readLine();
                interfaceInformation = aLine.split("\t");
                ivd = new InterfaceVersionData(interfaceInformation);
                _analysisToolInterfacesByBuildId.put(ivd.getBuildId(), ivd);
                _analysisToolInterfacesByInterfaceId.put(ivd.getInterfaceId(), ivd);
            }

            bufferReader.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

    public AnalysisToolVersionData(Vector data, ServerConnection svrConn)
	{
        Vector analysisToolInfo = (Vector) data.get(0);
		Vector analysisToolIFaceInfo = (Vector) data.get(1);

		_svrConn = svrConn;
		_svrPort = _svrConn.getServerPort();
		_user = _svrConn.getLoginName();

		_analysisToolId = (String) analysisToolInfo.get(0);
		_version = (Integer) analysisToolInfo.get(1);

		Vector anInterface;
		String bid, iid;
		InterfaceVersionData ivd;
		for (int i = 0; i < analysisToolIFaceInfo.size(); i++) {
			anInterface = (Vector) analysisToolIFaceInfo.get(i);
			iid = (String) anInterface.get(0);
			bid = (String) anInterface.get(2);
			ivd = new InterfaceVersionData(anInterface);
			_analysisToolInterfacesByBuildId.put(bid, ivd);
			_analysisToolInterfacesByInterfaceId.put(iid, ivd);
		}
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append(this._svrPort + "\t" + this._user + "\n");
        s.append("analysis tool:" + "\t" + this._analysisToolId +
                "\t" + this._version +
                "\t" + this._analysisToolInterfacesByBuildId.size() + "\n");

        ListIterator iterator = getInterfaces().listIterator();
        while (iterator.hasNext())
        {
            Object o = iterator.next();
            s.append(o.toString());
        }

        return s.toString();
    }

    public InterfaceVersionData getInterfaceVersionDataByBuildId(String buildId)
	{
		if (_analysisToolInterfacesByBuildId.containsKey(buildId))
			return (InterfaceVersionData) _analysisToolInterfacesByBuildId.get(buildId);
		return null;
	}

    public InterfaceVersionData getInterfaceVersionData(String interfaceId)
	{
		if (_analysisToolInterfacesByInterfaceId.containsKey(interfaceId))
			return (InterfaceVersionData) _analysisToolInterfacesByInterfaceId.get(interfaceId);
		return null;
	}

    public List getInterfaces()
	{
		return new ArrayList(_analysisToolInterfacesByInterfaceId.values());
	}

    public String getAnalysisToolId()
	{
		return _analysisToolId;
	}

    public String getServerPort()
	{
		return this._svrPort;
	}

    public String getUser()
    {
        return _user;
    }


}
