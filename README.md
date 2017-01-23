# README 

## Overview 
DOME is the Distributed Object-based Modeling Environment. It's purpose and goal was to provide a standard way to develop analytical models with causal relationships. The DOME client facilitates this, and provides a standard way to wrap and link in models in other languages or tools such as Python or Excel.

The toolkit also allows one to build so-called "integrated" models where multiple existing models can have their inputs and outputs mapped to a causal chain.

DOME also provides a mechanism for runnning these models in a standard way.

DOME was originally developed by MIT under a variety of funding sources in the 1990s. In 2011/2012, General Electric evolved the capability under funding from the Defense Advanced Research Projects Agency. 

## Getting DOME 
The DOME source code can be retrieved via its git repository or periodic releases at http://projectdmc.org/.

## Contributing 
If you're interested in contributing to the DOME source code base, please contact dmc@uilabs.org.

## Building DOME 
DOME is built using Ant.

To build the DOME client, execute the command:
*ant dist-client*

To build the DOME server, execute the command:
*ant dist-server*

To build the DOME war file, execute the command:
*ant dist-war*

The artifacts created by Ant can be removed by executing the command:
*ant clean*

## Running DOME 
DOME can also be run, after it is built, using Ant.

To run the DOME client, execute the command:
*ant run-client*

To run the DOME server, execute the command:
*ant run-server*

## License 
The DOME core package is distributed under an MIT/X11 open source license. See license.txt for the full copyright and license.

## Packaging Disclaimer 
For convenience, the source code of several dependencies has been bundled with the DOME core package. Many of these have their own licenses, and users of this software are responsible for understanding the implications of modifying this source code as part of the entire distribution. These include:

- The Unified Code for Units of Measure (UCUM) developed by The Regenstrief Institute under the GNU General Public License version 2 or later

- SkunkDAV, a WebDAV client built by Jacob Smullyan under GPL version 2 or later

- XML-RPC library developed by the Apache Software Foundation under the Apache Software License version 1.1 

- JFreeChart originally developed by David Gilbert and Andrzej Porebski (copyright Simba Management Limited) under the GNU Lesser General Public License  

- TouchGraph node/edge graphical rendering by TouchGraph LLC under a modified Apache License

- jquery developed by John Resig dual-licensed under the MIT or GPLv2 licenses