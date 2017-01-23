%Simulation of Quarter Car Suspension
%Created by J. Michael Gray and Jacob Wronski
%March 13th, 2003
function objective=quartercar1     %altered from Jacob's original file - hard coded the rest of the parameters

		%inserted  Input Parameters 
        	E=206.8*10^9; 		%Modulus of Elasticity, Units are Pa=N/m^2
			Mc=1800;    		%Mass of car, Units are kg
			g=9.81;    			%gravity constant, Units are m/s^2
			dw=.50;     		%Diamter of tire, Units are m
			u=.6;       		%Coefficient of Sliding Friction, Unitless
			Kt=161600;  		%Stiffness of tire, Units are N/m
			rho=8242.5;   		%Density of Steel, Units are kg/m^3
			SF=2;       		%Stress safety factor, it is Unitless
			L=.920;     		%length of axle shaft,Units are meters
			Mt=14;     			%Mass of tire kg
			Fr=0.05000;        	    %Size of the step function, Units are meters
			w=2*pi*2;        	%Frequency of sinusoidal input, Units are rad/sec
            MSuspension=60;     %Mass of suspension - wheel mass Units are kg
            
            %2. Differential Equation Simulation Parameters
		dt=0.0005;    		%Numerical intergration time increment, units are seconds 
		tmin=0;    			%Simulation Start time, measures in seconds 
		tmax=3;    			%Simulation Finishing time, measured in seconds 	
	    Tv=[tmin:dt:tmax];   %Creating the Time vector
		[t1,t2]=size(Tv);	%Finding the size of the time vector	
		U1=Fr*ones(t1,t2);  %First input function = a step input of magnitude Fr
		U2=Fr*sin(w*Tv);     %Second input function = a sinusoidal input, frequency w, amplitude Fr

	%3. Inequality Constraint Limits
		S=2*10^8;  	%Maximum Allowable Stress in axle, Units are Pa or N/m^2
		G=5;     	%Minimum Allowable Gas Mileage, Units are miles per gallon or mpg
		T=2.5;   	%Maximum Allowable Settling Time, Units are seconds
		F1=7.5;  	%Minimum Allowable Natural Frequency Ratio, Unitless
        F2=15;      %Maximum Allowable Natural Frequency Ratio, Unitless
		Co=2400;    %Maximum Allowable Cost
		%Wlimit is the Maximum Allowable Passenger Vertical Displacement Comfort Limit, 
		%units of Wlimit are meters.  Wlimit is calculated below after finding the damped natural frequency	
  
X0=[1.00;  0.5;  1.0;   1.0;  1.25;   .5;  1.5];
LB=[1.00;   .5;  .50;    .5;   .75;   .5;  .50];
UB=[1.50;  2.5; 1.25;   1.5;  1.50;   .7;  5.0];
            
            
		% Test -assign values to variables 
		%	LsIn;
		%	hsIn=0.5;
		%	bsIn=1.0;
		%	taIn=1.0;
		%	daoIn=1.25;
		%	LaIn=0.5;
		%	CsIn=1.5;

		% Temporarily set X vector equal to input variables 
			X1(1,1)=LsIn;
			X1(2,1)=hsIn*100;
			X1(3,1)=bsIn*10;
			X1(4,1)=  taIn*100;
			X1(5,1)=  daoIn*10;
			X1(6,1)=  LaIn;
		%	X1(7,1)= CsIn*1000;

%I. Inputs

		%1. Design Variables
		Ls=X1(1,1);  %Ls is the length of the leaf spring, Units are meters
		hs=X1(2,1)/100;  %hs is the thickness of the leaf spring, Units are meters
		bs=X1(3,1)/10;  %bs is the width of the leaf spring, Units are meters
		ta=X1(4,1)/100;  %ta is the thickness of the axle tube, Units are meters
		dao=X1(5,1)/10;  %dao is the outer diameter of the axle tube, Units are meters
		La=X1(6,1);  %La is the distance from the wheel to the leaf spring attach point, Units are meters
		% Cs=X1(7,1)*1000; %Cs is the damping coefficient of the shock absorber, Units are Ns/m
		Cs = 1000

       
%II. Simulation

	%1. Mass Module
		Ms=Mt + MSuspension;
		%Ms is the mass of the suspension, Units are kg

	%2. Spring Stiffness Module
		Ks=(4*E*bs*hs^3)/(Ls^3); 
		%Ks is the stiffness of the leaf spring, Units are N/m
	
   %3. Dynamics Module
   
		%A. Find the Natural Frequencies
			A1=(Mc/4)*Ms;
			B1=(Mc/4)*Ks+(Mc/4)*Kt+Ms*Ks;
			C1=Ks*Kt;
			Wnc=((B1-(B1^2-4*A1*C1)^.5)/(2*A1))^.5;
            Wns=((B1+(B1^2-4*A1*C1)^.5)/(2*A1))^.5;
            WnRatio=Wns/Wnc;
			%Wnc is the undamped natural frequency of the passenger cabin
			%Wns is the undamped natural frequency of the suspension	
            %WnRatio is the ratio of the two undamped natural frequencies
         
		%B. Find the Damping Ration, Damped Natural Frequency, and Settling Time
			Rc=(Cs/(2*(Ks*Mc)^.5));
	   	    Wdc=Wnc*(1-Rc^2)^.5;
  		 	St=1.8/(Rc*Wnc);
			%Rc is the damping ratio of the suspension system, it is unitless
   		    %Wdc is the Damped Natural Frequency of the Passenger Cabin, Units are rad/sec
   		    %St is the settling time for the suspension, Units are seconds
   
		%C. Create the A, B, C, and D matrices for use in the state-space form
			A11 = 0;
			A12 = 0;
			A13 = 1;
			A14 = 0;
			A21 = 0;
			A22 = 0;
			A23 = 0;
			A24 = 1;
			A31 = -4*Ks/Mc;
			A32 = 4*Ks/Mc;
			A33 = -4*Cs/Mc;
			A34 = 4*Cs/Mc;
			A41 = Ks/Ms;
			A42 = -(Ks+Kt)/Ms;
			A43 = Cs/Ms;
  		 	A44 = -Cs/Ms;
			A = [A11 A12 A13 A14; A21 A22 A23 A24; A31 A32 A33 A34; A41 A42 A43 A44];
			B = [0; 0; 0; Kt/Ms];
			C = [1 0 0 0;-1 1 0 0;A31 A32 A33 A34];
			D = zeros(3,1);

		%D. Solve for the State Space Form and execute the simulation "lsim"
			SYS = ss(A,B,C,D);
            lsim(SYS,U1,Tv);
            hold on;
            lsim(SYS,U2,Tv);
   	 	    [Y1 T1]=lsim(SYS,U1,Tv);
            [Y2 T2]=lsim(SYS,U2,Tv);
            Y1=abs(Y1);
            Maximum1=max(Y1);
            MaxAccel1=Maximum1(1,3);
			MaxDisp1=Maximum1(1,1)-Fr;
            Maximum2=max(Y2);
            MaxAccel2=Maximum2(1,3);
            Z=[zeros(((t2+1)/2),3*t1);ones((((t2+1)/2)-1),3*t1)];
            Y2=Y2.*Z;
            Maximum2=max(Y2);
   		    MaxDisp2=Maximum2(1,1);
            %MaxAccel1 is the maximum vertical acceleration of passenger cabin after step input
            %MaxDisp1 is the maximum vertical displacement of passenger cabin after step input
            %MaxAccel2 is the maximum vertical acceleration of passenger cabin after sinusoidal input
            %MaxDisp2 is the maximum steady state vertical displacement of passenger cabin after sinusoidal input

	%4. Stress Module
		sigma=(8*Mc*(g+MaxAccel1)*La*dao)/(pi*(dao^4-(dao-2*ta)^4)); %units are N/m^2
		tao=(4*Mc*g*u*dao*dw)/(pi*(dao^4-(dao-2*ta)^4)); %units are N/m^2
		taoxy=2*Mc*(g+MaxAccel1)/(pi*dao^2); %units are N/m^2
		max1=.5*(sigma+(4*tao^2+sigma^2)^.5); %units N/m^2
        max2=taoxy +tao;%units N/m^2s
        %sigma is the maximum bending stress
        %tao is the maximum shear stress due to torsion
        %taoxy is the maxiumum shear stress due to shear
		if max2 >= max1
  		   MaxStress=SF*max2; %units are Pa=N/m^2
		else
 		   MaxStress=SF*max1; %units are Pa=N/m^2
        end
        %MaxStress is the maximum stress times a safety factor found using Mohr's circle

	%5. Cost Module
		x1=1;
		x2=1;
		x3=1;
		x4=1;
      Cost=x1*(Mc+4*Ms)+x2*(1/Cs)^2+x3*(La)^2+x4*(Ls)^3;
      %Cost is the cost of the suspension, Units are dollars
      
   %6. Gas Mileage Model
       Mileage=25-.075*(4*Ms);
       %Mileage is the gas mileag of the vehicle, units are mpg
      
%III. Checking Constraints

   %1. Acceptable Stress Module
	   if MaxStress > S*1.001
         disp('Warning! Allowable stress exceeded by this design');
	   else
         disp('OK! Allowable stress not exceeded by this design');
      end
      
   %2. Acceptable Gas Mileage Module
		if Mileage*1.001 < G
   	   disp('Warning! min gas mileage limit exceeded by this design');
		else
     	   disp('OK! min gas mileage limit not exceeded by this design');
      end
      
   %3. Acceptable Settling Time
		if St > T*1.001
   	   disp('Warning! settling time limit exceeded by this design');
		else
   	   disp('OK! settling time limit not exceeded by this design');
      end
      
   %4. Acceptable Natural Frequency Ratio Module
		if WnRatio*1.001 <= F1 | WnRatio >= F2*1.001
   	   disp('Warning! natural frequency ratio limit exceeded by this design');
		else
   	   disp('OK! natural frequency ratio limit not exceeded by this design');
		end
      
   %5. Acceptable Cost Module
		if Cost > Co*1.001
  		   disp('Warning! max cost limit exceeded by this design');
		else
  		   disp('OK! max cost limit not exceeded by this design');
		end

	%6. Acceptable Passenger Comfort Module
		W=1.591*Wdc^-2.1995;
		if MaxDisp2 > W*1.001
  			disp('Warning! passenger comfort limit exceeded by this design');
		else
  			disp('OK! passenger comfort limit not exceeded by this design');
		end

	 
%IV. Output

	%1. Constrained and Dependent Variables
      disp('');
      disp('Dependent Variables');
      Ms
	  Ks
      Rc
      Wdc
      W
      disp('Constrained Variables');
      MaxStress
      Mileage
      St
      WnRatio
      Cost
	  MaxDisp2
      MaxAccel1

	%2. Objective
    objective=MaxAccel1;