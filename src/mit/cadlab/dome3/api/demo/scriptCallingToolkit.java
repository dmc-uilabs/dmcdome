package mit.cadlab.dome3.api.demo;

import mit.cadlab.dome3.api.*;

public class scriptCallingToolkit {
    public static void main(String[] args) {
        /* open a connection and log in to DOME server */
        DomeConnection conn = new DomeConnection("guest", "", "18.80.5.196:8080");

        /* browse folders, models and Interfaces */
        DomeFolder radiationFolder = conn.getPublicFolder().getFolder("solar radiation");
        DomeModel solarAngleModel = radiationFolder.getModelByName("sun-earth geometric relationship");
        DomeInterface solarAngleInterface = solarAngleModel.getInterfaceByName("solar altitude and azimuth Interface");
        DomeModel radiationModel = radiationFolder.getModelByName("solar radiation on tilted surface");
        DomeInterface radiationInterface = radiationModel.getInterfaceByName("solar radiation on tilted surface - complete Interface");

        /* instantiate Runtime Interfaces */
        RuntimeInterface runtimeAngleIface = solarAngleInterface.createRuntimeInterface();
        RuntimeInterface runtimeRadIface = radiationInterface.createRuntimeInterface();

        // load the parameters of sun-earth geometric relationship model
        RuntimeParameter dayNumParam = runtimeAngleIface.getParameterByName("day number");
        RuntimeParameter timeParam = runtimeAngleIface.getParameterByName("local standard time");

        // load the parameters of solar radiation on tilted surface model
        RuntimeParameter panelAzimuthParam = runtimeRadIface.getParameterByName("panel azimuth angle");
        RuntimeParameter altitudeParam = runtimeRadIface.getParameterByName("solar altitude angle");
        RuntimeParameter azimuthParam = runtimeRadIface.getParameterByName("solar azimuth angle");

        // set panel azimuth angle to point to south
        panelAzimuthParam.setRealValue(0);

        // set date to jan 1st
        dayNumParam.setRealValue(1);
        for (int hr = 0; hr < 24; hr++) {       // iterate through hr
            System.out.print("     hour: " + hr);
            // run the sun-earth geometric relationship with new inputs
            timeParam.setRealValue(hr);
            runtimeAngleIface.submit();         // execute the angle interface
            // get the resulted angles
            double altitude = runtimeAngleIface.getParameterByName("solar altitude").getRealValue();
            double azimuth = runtimeAngleIface.getParameterByName("solar azimuth").getRealValue();

            // now run the solar radiation on tilted surface model with the new angles
            altitudeParam.setRealValue(altitude);
            azimuthParam.setRealValue(azimuth);
            runtimeRadIface.submit();
            double rad = runtimeRadIface.getParameterByName("total tilted radiation").getRealValue();
            System.out.println("    total rad: " + (rad > 0 ? rad : 0));
        }

        /* log out and close connection */
        conn.close();
    }
}



