package mit.cadlab.dome3.search.similarityassessment.analyse_for_attributes;

/**
 * User: Sangmok Han
 * Date: 2005. 3. 4.
 */
public class CommonUnit {
	public static String DATA  =
"#" + "\n" +
"# Copyright (c) 1998 The Regenstrief Institute.  All rights reserved." + "\n" +
"#" + "\n" +
"# This program is free software; you can redistribute it and/or modify" + "\n" +
"# it under the terms of the GNU General Public License as published by" + "\n" +
"# the Free Software Foundation; either version 2 of the License, or" + "\n" +
"# (at your option) any later version." + "\n" +
"#" + "\n" +
"# This program is distributed in the hope that it will be useful," + "\n" +
"# but WITHOUT ANY WARRANTY; without even the implied warranty of" + "\n" +
    "# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the" + "\n" +
"# GNU General Public License for more details." + "\n" +
"#" + "\n" +
"# You should have received a copy of the GNU General Public License" + "\n" +
"# along with this program; if not, write to the Free Software" + "\n" +
"# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA." + "\n" +
"#" + "\n" +
"#######################################################################" + "\n" +
"#" + "\n" +
"# The Unified Code for Units of Measure (UCUM)" + "\n" +
"#" + "\n" +
"# This file is generated automatically. Please refer to the original" + "\n" +
"# UCUM specification at" + "\n" +
"#" + "\n" +
"#		http://aurora.rg.iupui.edu/UCUM" + "\n" +
"#" + "\n" +
"case sensitive" + "\n" +
"prefix Y 1E24" + "\n" +
"prefix Z 1E21" + "\n" +
"prefix E 1E18" + "\n" +
"prefix P 1E15" + "\n" +
"prefix T 1E12" + "\n" +
"prefix G 1E9" + "\n" +
"prefix M 1E6" + "\n" +
"prefix k 1E3" + "\n" +
"prefix h 1E2" + "\n" +
"prefix da 1E1" + "\n" +
"prefix d 1E-1" + "\n" +
"prefix c 1E-2" + "\n" +
"prefix m 1E-3" + "\n" +
"prefix u 1E-6" + "\n" +
"prefix n 1E-9" + "\n" +
"prefix p 1E-12" + "\n" +
"prefix f 1E-15" + "\n" +
"prefix a 1E-18" + "\n" +
"prefix z 1E-21" + "\n" +
"prefix y 1E-24" + "\n" +
"prefix Ki 1024" + "\n" +
"prefix Mi 1048576" + "\n" +
"prefix Gi 1073741824" + "\n" +
"prefix Ti 1099511627776" + "\n" +
"dimensions 10" + "\n" +
"base m | length | meter # some notes" + "\n" +
"base g | mass | gram" + "\n" +
"base s | time | second" + "\n" +
"base rad | plane angle | radian" + "\n" +
"base K | temperature | Kelvin" + "\n" +
"base C | electric charge | Coulomb" + "\n" +
"base cd | luminous intensity | candela" + "\n" +
"base $ | currency | dollar" + "\n" +
"base individual | optimization | individual" + "\n" +
"base objective | optimization | objective" + "\n" +
"#10* = 10 1 nonmetric | constant | 10^ # for arbitrary powers # some notes" + "\n" +
"No_Unit = 1 1 nonmetric | no unit | no unit" + "\n" +
"Dless = 1 1 nonmetric | dimensionless | dimensionless" + "\n" +
"% = 1E-2 1 nonmetric | constant | percent" + "\n" +
"[pi] = 3.1415926535897932384626433832795028841971693993751058209749445923 1 nonmetric | constant | pi" + "\n" +
"[g] = 9.80665 m/s2 metric | constant | g (gravitational acceleration)" + "\n" +
"sr = 1 rad2 metric | solid angle | streadian # some notes" + "\n" +
"Hz = 1 s-1 metric | frequency | Herz" + "\n" +
"N = 1 kg.m/s2 metric | force | Newton" + "\n" +
"Pa = 1 N/m2 metric | pressure | Pascal" + "\n" +
"J = 1 N.m metric | energy | Joule" + "\n" +
"W = 1 J/s metric | power | watt" + "\n" +
"A = 1 C/s metric | electric current | Amp`ere" + "\n" +
"V = 1 J/C metric | electric potential | Volt" + "\n" +
"F = 1 C/V metric | electric capacitance | Farad" + "\n" +
"Ohm = 1 V/A metric | electric resistance | Ohm" + "\n" +
"S = 1 Ohm-1 metric | electric conductance | Siemens" + "\n" +
"Wb = 1 V.s metric | magnetic flux | Weber" + "\n" +
"Cel = cel(1 K) metric | temperature | degree Celsius #some notes" + "\n" +
"T = 1 Wb/m2 metric | magnetic flux density | Tesla" + "\n" +
"H = 1 Wb/A metric | inductance | Henry" + "\n" +
"lm = 1 cd.sr metric | luminous flux | lumen" + "\n" +
"lx = 1 lm/m2 metric | illuminance | lux" + "\n" +
"Bq = 1 s-1 metric | radioactivity | Becquerel" + "\n" +
"Gy = 1 J/kg metric | energy dose | Gray" + "\n" +
"Sv = 1 J/kg metric | dose equivalent | Sievert" + "\n" +
"deg = 2 [pi].rad/360 nonmetric | plane angle | degree" + "\n" +
"' = 1 deg/60 nonmetric | plane angle | minute" + "\n" +
"'' = 1 '/60 nonmetric | plane angle | second" + "\n" +
"gon = 0.9 deg nonmetric | plane angle | gon (grade)" + "\n" +
"l = 1 dm3 metric | volume | liter" + "\n" +
"ar = 100 m2 metric | area | are" + "\n" +
"min = 60 s nonmetric | time | minute" + "\n" +
"h = 60 min nonmetric | time | hour" + "\n" +
"d = 24 h nonmetric |time | day" + "\n" +
"a_t = 365.24219 d nonmetric | time | year (tropical)" + "\n" +
"a_j = 365.25 d nonmetric | time | year (mean Julian)" + "\n" +
"a_g = 365.2425 d nonmetric | time | year (mean Gregorian)" + "\n" +
"a = 1 a_j nonmetric | time | year" + "\n" +
"wk = 7 d nonmetric | time | week" + "\n" +
"mo_s = 29.53059 d nonmetric | time | month (synodal)" + "\n" +
"mo_g = 1 a_g/12 nonmetric | time | month (Gregorian)" + "\n" +
"mo_j = 1 a_j/12 nonmetric | time | month (Julian)" + "\n" +
"mo = 1 mo_j nonmetric | time | month" + "\n" +
"t = 1E3 kg metric | mass | tonne" + "\n" +
"slug = 14.5939 kg nonmetric | mass | slug" + "\n" +
"bar = 1E5 Pa metric | pressure | bar" + "\n" +
"[e] = 1.60217733E-19 C metric | electric charge | elementary charge" + "\n" +
"eV = 1 [e].V metric | energy | electronvolt" + "\n" +
"u = 1.6605402E-24 g metric | mass | unified atomic mass unit" + "\n" +
"AU = 149597.870 Mm nonmetric | length | astronomic unit" + "\n" +
"pc = 3.085678E16 m metric | length | parsec" + "\n" +
"[c] = 299792458 m/s metric | velocity | velocity of light" + "\n" +
"[h] = 6.6260755E-34 J.s metric | constant | Planck constant" + "\n" +
"[k] = 1.380658E-23 J/K metric | constant | Boltzmann constant" + "\n" +
"[sigma] = 5.670400E-8 W/m2/K4 metric | constant | Stefan-Boltzmann constant" + "\n" +
"[eps_0] = 8.854187817E-12 F/m metric | constant | permittivity of vacuum" + "\n" +
"[m_e] = 9.1093897E-28 g metric | mass | electron mass" + "\n" +
"[mu_0] = 1.2566370614359172953850573533118E-6 N/A2 metric | constant | permeability of vacuum" + "\n" +
"[m_p] = 1.6726231E-24 g metric | mass | proton mass" + "\n" +
"[G] = 6.67259E-11 m3.kg-1.s-2 metric | constant | Newtonian constant of gravitation" + "\n" +
"[ly] = 1 [c].a_j metric | length | light-year" + "\n" +
"Ky = 1 cm-1 metric | lineic number | Kayser" + "\n" +
"Gal = 1 cm/s2 metric | acceleration | Gal" + "\n" +
"dyn = 1 g.cm/s2 metric | force | dyne" + "\n" +
"erg = 1 dyn.cm metric | energy | erg" + "\n" +
"P = 1 dyn.s/cm2 metric | dynamic viscosity | Poise" + "\n" +
"St = 1 cm2/s metric | kinematic viscosity | Stokes" + "\n" +
"Mx = 1E-8 Wb metric | flux of magnetic induction | Maxwell" + "\n" +
"G = 1E-4 T metric | magnetic flux density | Gauss" + "\n" +
"Oe = 250 /[pi].A/m metric | magnetic field intensity | Oersted" + "\n" +
"Gb = 1 Oe.cm metric | magnetic tension | Gilbert" + "\n" +
"sb = 1 cd/cm2 metric | lum. intensity density | stilb" + "\n" +
"Lmb = 1 cd/cm2/[pi] metric | brightness | Lambert" + "\n" +
"ph = 1E-4 lx metric | illuminance | phot" + "\n" +
"Ci = 3.7E10 Bq metric | radioactivity | Curie" + "\n" +
"R = 2.58E-4 C/kg metric | ion dose | Roentgen" + "\n" +
"RAD = 100 erg/g metric | energy dose | radiation absorbed dose" + "\n" +
"REM = 1 RAD metric | dose equivalent | radiation equivalent man" + "\n" +
"[in_i] = 2.54 cm nonmetric | length | inch (Int'l)" + "\n" +
"[mesh_i] = 1 /[in_i] nonmetric | | mesh (Int'l)" + "\n" +
"[hd_i] = 4 [in_i] nonmetric | height of horses | hand (Int'l)" + "\n" +
"[ft_i] = 12 [in_i] nonmetric | length | foot (Int'l)" + "\n" +
"[yd_i] = 3 [ft_i] nonmetric | length | yard (Int'l)" + "\n" +
"[mi_i] = 5280 [ft_i] nonmetric | length | statute mile (Int'l)" + "\n" +
"[mil_i] = 1E-3 [in_i] nonmetric | length | mil (Int'l)" + "\n" +
"[fth_i] = 6 [ft_i] nonmetric | depth of water | fathom (Int'l)" + "\n" +
"[nmi_i] = 1852 m nonmetric | length | nautic mile (Int'l)" + "\n" +
"[kn_i] = 1 [nmi_i]/h nonmetric | velocity | knot (Int'l)" + "\n" +
"[sin_i] = 1 [in_i]2 nonmetric | area | square inch (Int'l)" + "\n" +
"[sft_i] = 1 [ft_i]2 nonmetric | area | square foot (Int'l)" + "\n" +
"[syd_i] = 1 [yd_i]2 nonmetric | area | square yard (Int'l)" + "\n" +
"[cin_i] = 1 [in_i]3 nonmetric | volume | cubic inch (Int'l)" + "\n" +
"[cft_i] = 1 [ft_i]3 nonmetric | volume | cubic foot (Int'l)" + "\n" +
"[bf_i] = 144 [in_i]3 nonmetric | volume | board foot (Int'l)" + "\n" +
"[cr_i] = 128 [ft_i]3 nonmetric | volume | cord (Int'l)" + "\n" +
"[cyd_i] = 1 [yd_i]3 nonmetric | volume | cubic yard (Int'l)" + "\n" +
"[cml_i] = 1 [pi]/4.[mil_i]2 nonmetric | area | circular mil (Int'l)" + "\n" +
"[ft_us] = 1200 m/3937 nonmetric | length | foot (U.S.)" + "\n" +
"[yd_us] = 3 [ft_us] nonmetric | length | yard (U.S.)" + "\n" +
"[in_us] = 1 [ft_us]/12 nonmetric | length| inch (U.S.)" + "\n" +
"[rd_us] = 16.5 [ft_us] nonmetric | length | rod (U.S.)" + "\n" +
"[ch_us] = 4 [rd_us] nonmetric | length | chain (Gunter's or Surveyor's) (U.S.)" + "\n" +
"[lk_us] = 1 [ch_us]/100 nonmetric | length | link (for Gunter's chain) (U.S.)" + "\n" +
"[rch_us] = 100 [ft_us] nonmetric | length | chain (Ramden's or Engineer's) (U.S.)" + "\n" +
"[rlk_us] = 1 [rch_us]/100 nonmetric | length | link (for Ramden's chain) (U.S.)" + "\n" +
"[fth_us] = 6 [ft_us] nonmetric | length | fathom (U.S.)" + "\n" +
"[fur_us] = 40 [rd_us] nonmetric | length | furlong (U.S.)" + "\n" +
"[mi_us] = 8 [fur_us] nonmetric | length | mile (U.S.)" + "\n" +
"[acr_us] = 160 [rd_us]2 nonmetric | area | acre (U.S.)" + "\n" +
"[srd_us] = 1 [rd_us]2 nonmetric | area | square rod (U.S.)" + "\n" +
"[smi_us] = 1 [mi_us]2 nonmetric | area | square mile (U.S.)" + "\n" +
"[mil_us] = 1E-3 [in_us] nonmetric | length | mil (U.S.)" + "\n" +
"[sct] = 1 [mi_us]2 nonmetric | area | section" + "\n" +
"[twp] = 36 [sct] nonmetric | area | township" + "\n" +
"[in_br] = 2.539998 cm nonmetric | length | inch (Brit.)" + "\n" +
"[ft_br] = 12 [in_br] nonmetric | length | foot (Brit.)" + "\n" +
"[rd_br] = 16.5 [ft_br] nonmetric | length | rod (Brit.)" + "\n" +
"[ch_br] = 4 [rd_br] nonmetric | length | chain (Gunter's chain) (Brit.)" + "\n" +
"[lk_br] = 1 [ch_br]/100 nonmetric | length | link (for Gunter's chain) (Brit.)" + "\n" +
"[fth_br] = 6 [ft_br] nonmetric | length | fathom (Brit.)" + "\n" +
"[pc_br] = 2.5 [ft_br] nonmetric | length | pace (Brit.)" + "\n" +
"[yd_br] = 3 [ft_br] nonmetric | length | yard (Brit.)" + "\n" +
"[mi_br] = 5280 [ft_br] nonmetric | length | mile (Brit.)" + "\n" +
"[nmi_br] = 6080 [ft_br] nonmetric | length | nautic mile (Brit.)" + "\n" +
"[kn_br] = 1 [nmi_br]/h nonmetric | velocity | knot (Brit.)" + "\n" +
"[acr_br] = 4840 [yd_br]2 nonmetric | area | acre (Brit.)" + "\n" +
"[gal_us] = 231 [in_i]3 nonmetric | volume (liquid) | gallon (Queen Anne's wine gallon) (U.S.)" + "\n" +
"[bbl_us] = 42 [gal_us] nonmetric | volume (liquid) | barrel (U.S.)" + "\n" +
"[qt_us] = 1 [gal_us]/4 nonmetric | volume (liquid) | quart (U.S.)" + "\n" +
"[pt_us] = 1 [qt_us]/2 nonmetric | volume (liquid) | pint (U.S.)" + "\n" +
"[gil_us] = 1 [pt_us]/4 nonmetric | volume (liquid) | gill (U.S.)" + "\n" +
"[foz_us] = 1 [gil_us]/4 nonmetric | volume (liquid) | fluid ounce (U.S.)" + "\n" +
"[fdr_us] = 1 [foz_us]/8 nonmetric | volume (liquid) | fluid dram (U.S.)" + "\n" +
"[min_us] = 1 [fdr_us]/60 nonmetric | volume (liquid) | minim (U.S.)" + "\n" +
"[crd_us] = 128 [ft_i]3 nonmetric | volume (liquid) | cord (U.S.)" + "\n" +
"[bu_us] = 2150.42 [in_i]3 nonmetric | volume (dry) | bushel (U.S.)" + "\n" +
"[pk_us] = 1 [bu_us]/4 nonmetric | volume (dry) | peck (U.S.)" + "\n" +
"[dqt_us] = 1 [pk_us]/8 nonmetric | volume (dry) | dry quart (U.S.)" + "\n" +
"[dpt_us] = 1 [dqt_us]/2 nonmetric | volume (dry) | dry pint (U.S.)" + "\n" +
"[gal_wi] = 1 [bu_us]/8 nonmetric | volume (dry) | winchester gallon (historical)" + "\n" +
"[gal_br] = 4.54609 l nonmetric | volume | gallon (Brit.)" + "\n" +
"[pk_br] = 2 [gal_br] nonmetric | volume | bushel (Brit.)" + "\n" +
"[bu_br] = 4 [pk_br] nonmetric | volume | peck (Brit.)" + "\n" +
"[qt_br] = 1 [gal_br]/4 nonmetric | volume | quart (Brit.)" + "\n" +
"[pt_br] = 1 [qt_br]/2 nonmetric | volume | pint (Brit.)" + "\n" +
"[gil_br] = 1 [pt_br]/4 nonmetric | volume | gill (Brit.)" + "\n" +
"[foz_br] = 1 [gil_br]/5 nonmetric | volume | fluid ounce (Brit.)" + "\n" +
"[fdr_br] = 1 [foz_br]/8 nonmetric | volume | fluid dram (Brit.)" + "\n" +
"[min_br] = 1 [fdr_br]/60 nonmetric | volume | minim (Brit.)" + "\n" +
"[tbs_us] = 1 [foz_us]/2 nonmetric | volume | tablespoon (U.S.)" + "\n" +
"[tsp_us] = 1 [tbs_us]/3 nonmetric | volume | teaspoon (U.S.)" + "\n" +
"[cup_us] = 16 [tbs_us] nonmetric | volume | cup (U.S.)" + "\n" +
"[gr] = 64.79891 mg nonmetric | mass | grain" + "\n" +
"[lb_av] = 7000 [gr] nonmetric | mass | pound (av)" + "\n" +
"[oz_av] = 1 [lb_av]/16 nonmetric | mass | ounce (av)" + "\n" +
"[dr_av] = 1 [oz_av]/16 nonmetric | mass | dram (av)" + "\n" +
"[scwt_av] = 100 [lb_av] nonmetric | mass | short hundredweight (U.S.)" + "\n" +
"[lcwt_av] = 112 [lb_av] nonmetric | mass | long hunderdweight (Brit.)" + "\n" +
"[ston_av] = 20 [scwt_av] nonmetric | mass | short ton (U.S.)" + "\n" +
"[lton_av] = 20 [lcwt_av] nonmetric | mass | long ton (Brit.)" + "\n" +
"[stone_av] = 14 [lb_av] nonmetric | mass | stone (Brit.)" + "\n" +
"[lbf_av] = 1 [lb_av].[g] nonmetric | force | pound force (av)" + "\n" +
"[pwt_tr] = 24 [gr] nonmetric | mass | pennyweight (tr)" + "\n" +
"[oz_tr] = 20 [pwt_tr] nonmetric | mass | ounce (tr)" + "\n" +
"[lb_tr] = 12 [oz_tr] nonmetric | mass | pound (tr)" + "\n" +
"[sc_ap] = 20 [gr] nonmetric | mass | scruple (ap)" + "\n" +
"[dr_ap] = 3 [sc_ap] nonmetric | mass | dram (drachm) (ap)" + "\n" +
"[oz_ap] = 8 [dr_ap] nonmetric | mass | ounce (ap)" + "\n" +
"[lb_ap] = 12 [oz_ap] nonmetric | mass | pound (ap)" + "\n" +
"circ = 2 [pi].rad nonmetric | plane angle | circle" + "\n" +
"sph = 4 [pi].sr nonmetric | solid angle | spere" + "\n" +
"[degF] = degf(5 K/9) nonmetric | temperature | degree Fahrenheit" + "\n" +
"Np = ln(1 1) metric | level | neper" + "\n" +
"B = lg(1 1) metric | level | bel" + "\n" +
"[dB] = 1 dB metric | level | decibel" + "\n" +
"B[SPL] = 2lg(2E-5 Pa) metric | pressure level | bel sound pressure" + "\n" +
"B[V] = 2lg(1 V) metric | electric potential level | bel volt" + "\n" +
"B[mV] = 2lg(1 mV) metric | electric potential level | bel milivolts" + "\n" +
"B[uV] = 2lg(1 uV) metric | electric potential level | bel microvolts" + "\n" +
"B[W] = lg(1 W) metric | power level | bel watts" + "\n" +
"B[kW] = lg(1 kW) metric | power level | bel kilowatts" + "\n" +
"mol = 6.0221367E23 1 metric | amount of substance | mole" + "\n" +
"eq = 1 mol metric | amount of substance | equivalents" + "\n" +
"[pH] = pH(1 mol/l) nonmetric | acidity | pH" + "\n" +
"kat = 1 mol/s metric | catalytic activity | katal" + "\n" +
"U = 1 umol/min metric | catalytic activity | Unit" + "\n" +
"osm = 1 mol metric | amount of substance | osmole (dissolved particles)" + "\n" +
"[drp] = 1 ml/12 nonmetric | volume | drop" + "\n" +
"[diop] = 1 /m nonmetric | refraction of a lens | diopter" + "\n" +
"[Ch] = 1 mm/[pi] nonmetric | gauge of catheters | Charri`ere (french)" + "\n" +
"[lne] = 1 [in_i]/12 nonmetric | length | line" + "\n" +
"[pnt] = 1 [lne]/6 nonmetric | length | point" + "\n" +
"[pca] = 12 [pnt] nonmetric | length | pica" + "\n" +
"[pnt_pr] = 0.013837 [in_i] nonmetric | length | Printer's point" + "\n" +
"[pca_pr] = 12 [pnt_pr] nonmetric | length | Printer's pica" + "\n" +
"[pied] = 32.48 cm nonmetric | length | pied (French foot)" + "\n" +
"[pouce] = 1 [pied]/12 nonmetric | length | pouce (French inch)" + "\n" +
"[ligne] = 1 [pouce]/12 nonmetric | length | ligne (French line)" + "\n" +
"[didot] = 1 [ligne]/6 nonmetric | length | didot (Didot's point)" + "\n" +
"[cicero] = 12 [didot] nonmetric | length | cicero (Didot's pica)" + "\n" +
"st = 1 m3 metric | volume | stere" + "\n" +
"Ao = 0.1 nm nonmetric | length | Angstrom" + "\n" +
"b = 100 fm2 nonmetric | area (action) | barn" + "\n" +
"atm = 101325 Pa nonmetric | pressure | atmosphere (standard)" + "\n" +
"gf = 1 g.[g] metric | force | gram-force" + "\n" +
"att = 1 kgf/cm2 nonmetric | pressure | atmosphere (technical)" + "\n" +
"[psi] = 1 [lbf_av]/[in_i]2 nonmetric | pressure | psi" + "\n" +
"[S] = 1E-13 s nonmetric | sedimentation coefficient | Svedberg unit" + "\n" +
"m[H2O] = 9.80665 kPa metric | pressure | meter of water column" + "\n" +
"m[Hg] = 133.3220 kPa metric | pressure | meter of mercury column" + "\n" +
"[in_i'H2O] = 1 m[H2O].[in_i]/m nonmetric | pressure | inch of water column" + "\n" +
"[in_i'Hg] = 1 m[Hg].[in_i]/m nonmetric | pressure | inch of mercury column" + "\n" +
"[car_m] = 0.2 g nonmetric | mass | carat (metric)" + "\n" +
"[car_Au] = 1 /24 nonmetric | mass fraction | carat (of gold alloys)" + "\n" +
"[HPF] = 1 1 nonmetric | view area in microscope | high power field" + "\n" +
"[LPF] = 100 1 nonmetric | view area in microscope | low power field" + "\n" +
"[hnsf'U] = 1 1 nonmetric | x-ray attenuation | Hounsfield unit" + "\n" +
"[MET] = 3.5 ml/min/kg nonmetric | metabolic cost of physical activity | metabolic equivalent" + "\n" +
"[PRU] = 1 mm[Hg].s/ml nonmetric | fluid resistance | peripheral vascular resistance unit" + "\n" +
"[iU] = 1 1 metric | arbitrary | international unit" + "\n" +
"[arb'U] = 1 1 nonmetric | arbitrary | arbitary unit" + "\n" +
"[USP'U] = 1 1 nonmetric | arbitrary | United States Pharmacopeia unit" + "\n" +
"[GPL'U] = 1 1 nonmetric | biologic activity | GPL unit (anticardiolipin IgG)" + "\n" +
"[MPL'U] = 1 1 nonmetric | biologic activity | MPL unit (anticardiolipin IgM)" + "\n" +
"[APL'U] = 1 1 nonmetric | biologic activity | APL unit (anticardiolipin IgA)" + "\n" +
"[beth'U] = 1 1 nonmetric | biologic activity | Bethesda unit (factor VII inhibitor)" + "\n" +
"[todd'U] = 1 1 nonmetric | biologic activity | Todd unit (antistreptolysin O)" + "\n" +
"[dye'U] = 1 1 nonmetric | biologic activity | Dye unit (amylase)" + "\n" +
"[smgy'U] = 1 1 nonmetric | biologic activity | Somogyi unit (amylase)" + "\n" +
"[bdsk'U] = 1 1 nonmetric | biologic activity | Bodansky unit (phosphatase)" + "\n" +
"[ka'U] = 1 1 nonmetric | biologic activity | King-Armstrong unit (phosphatase)" + "\n" +
"[knk'U] = 1 1 nonmetric | biologic activity | Kunkel unit" + "\n" +
"[mclg'U] = 1 1 nonmetric | biologic activity | Mac Lagan unit" + "\n" +
"[tb'U] = 1 1 nonmetric | biologic activity | tuberculin unit (tuberculin)" + "\n" +
"[ppb] = 1E-9 1 nonmetric | constant| parts per billion" + "\n" +
"[ppm] = 1E-6 1 nonmetric | constant| parts per million" + "\n" +
"[ppth] = 1E-3 1 nonmetric | constant| parts per thousand" + "\n" +
"[pptr] = 1E-12 1 nonmetric | constant| parts per trillion" + "\n" +
"kJ = 1 kJ metric | energy | kiloJoule" + "\n" +
"MJ = 1 MJ metric | energy | megaJoule" + "\n" +
"cal_[15] = 4.18580 J metric | energy | calorie (qu15:degC)" + "\n" +
"cal_[20] = 4.18190 J metric | energy | calorie (qu20:degC)" + "\n" +
"cal_m = 4.19002 J metric | energy | calorie (mean)" + "\n" +
"cal_IT = 4.1868 J metric | energy | calorie (intl. table)" + "\n" +
"cal_th = 4.184 J metric | energy | calorie (thermochemical)" + "\n" +
"cal = 1 cal_th metric | energy | calorie (usual)" + "\n" +
"[Cal] = 1 kcal_th nonmetric | energy | Calorie (nutrition)" + "\n" +
"[Btu_39] = 1.05967 kJ nonmetric | energy | British thermal unit (qu39:degF)" + "\n" +
"[Btu_59] = 1.05480 kJ nonmetric | energy | Btu (qu59:degF)" + "\n" +
"[Btu_60] = 1.05468 kJ nonmetric | energy | Btu (qu60:degF)" + "\n" +
"[Btu_m] = 1.05587 kJ nonmetric | energy | Btu (mean)" + "\n" +
"[Btu_IT] = 1.05505585262 kJ nonmetric | energy | Btu (intl. table)" + "\n" +
"[Btu_th] = 1.054350 kJ nonmetric | energy | Btu (thermochemical)" + "\n" +
"[Btu] = 1 [Btu_th] nonmetric | energy | Btu (usual)" + "\n" +
"[HP] = 550 [ft_i].[lbf_av]/s nonmetric | power | horsepower" + "\n" +
"bit_s = ld(1 1) nonmetric | amount of information | bit (logarithmus dualis of the number of distinct signals)" + "\n" +
"bit = 1 1 metric | amount of information | bit" + "\n" +
"By = 8 bit metric | amount of information | byte" + "\n" +
"Bd = 1 /s metric | signal transmission rate | baud" + "\n" +
"km = 1 km metric | length | kilometer" + "\n" +
"cm = 1 cm metric | length | centimeter" + "\n" +
"mm = 1 mm metric | length | millimeter" + "\n" +
"um = 1 um metric | length | micrometer" + "\n" +
"nm = 1 nm metric | length | nanometer" + "\n" +
"kg = 1 kg metric | mass | kilogram" + "\n" +
"mg = 1 mg metric | mass | milligram" + "\n" +
"ms = 1 ms metric | time | millisecond" + "\n" +
"ns = 1 ns metric | time | nanosecond" + "\n" +
"uC = 1 uC metric | electric charge | microCoulomb" + "\n" +
"[skm] = 1 km2 metric | area | square kilometer" + "\n" +
"[sm] = 1 m2 metric | area | square meter" + "\n" +
"[scm] = 1 cm2 metric | area | square centimeter" + "\n" +
"[smm] = 1 mm2 metric | area | square millimeter" + "\n" +
"[c_km] = 1 km3 metric | volume | cubic kilometer" + "\n" +
"[c_m] = 1 m3 metric | volume | cubic meter" + "\n" +
"[c_cm] = 1 cm3 metric | volume | cubic centimeter" + "\n" +
"[c_mm] = 1 mm3 metric | volume | cubic millimeter" + "\n" +
"ml = 1 ml metric | volume | milliliter" + "\n" +
"[RPM] = 1 /min nonmetric | angular velocity | revolutions per minute" + "\n" +
"[DPS] = 1 deg/min nonmetric | angular velocity | degree per second" + "\n" +
"[DPH] = 1 deg/h nonmetric | angular velocity | degree per hour" + "\n" +
"[RPS] = 1 rad/min metric | angular velocity | radian per second" + "\n" +
"[MiPH] = 1 [mi_i]/h nonmetric | velocity | mile per hour (Int'l)" + "\n" +
"[KMPH] = 1 km/h metric | velocity | kilometer per hour" + "\n" +
"[FtPS] = 1 [ft_i]/s nonmetric | velocity | foot per second (Int'l)" + "\n" +
"[InPM] = 1 [in_i]/min nonmetric | velocity | inch per minute (Int'l)" + "\n" +
"[InPS] = 1 [in_i]/s nonmetric | velocity | inch per second (Int'l)" + "\n" +
"[FtPM] = 1 [ft_i]/min nonmetric | velocity | foot per minute (Int'l)" + "\n" +
"[FPs_S] = 1 [ft_i]/s2 nonmetric | acceleration | foot per second square (Int'l)" + "\n" +
"[MPS] = 1 m/s metric| velocity | meter per second" + "\n" +
"[CMPS] = 1 cm/s metric | velocity | centimeter per second" + "\n" +
"[MMPS] = 1 mm/s metric | velocity | millimeter per second" + "\n" +
"[CMPM] = 1 cm/min metric | velocity | centimeter per minute" + "\n" +
"[MMPM] = 1 mm/min metric | velocity | millimeter per minute" + "\n" +
"[MPs_S] = 1 m/s2 metric | acceleration | meter per second square" + "\n" +
"[NPMM] = 1 N/mm metric | stiffness | Newton per millimeter" + "\n" +
"[lbf_av_p_ft] = 1 [lbf_av]/[ft_i] nonmetric | stiffness | Pound force per foot" + "\n" +
"[NPM] = 1 N/m metric | | Newton per meter" + "\n" +
"[NSPM] = 1 N.s/m metric | | Newton second per meter" + "\n" +
"[MPNS] = 1 m/ns metric | velocity | meter per nanosecond" + "\n" +
"[MSPM] = 1 mS/m metric | conductivity | milli-Siemens per meter" + "\n" +
"mm[Hg] = 1 mm[Hg] metric | pressure | millimeter of mercury column" + "\n" +
"mbar = 1 mbar metric | pressure | millibar" + "\n" +
"[cc] = 1 ml metric | volume | cc" + "\n" +
"mrad = 1 mrad metric | plane angle | milliradian" + "\n" +
"[kW_h] = 1 kW.h nonmetric | energy | kilowatt-hour" + "\n" +
"[MW_h] = 1 MW.h nonmetric | energy | megawatt-hour" + "\n" +
"[W_h] = 1 W.h nonmetric | energy | watt-hour" + "\n" +
"[kW_a] = 1 kW.a nonmetric | energy | kilowatt-year" + "\n" +
"[MBtu_th] = 1E6 [Btu_th] nonmetric | energy | megaBtu (thermochemical)" + "\n" +
"[m_p_s_p_K] = 1 m/s/K metric | velocity change with temperature | meter per second per Kelvin" + "\n" +
"[m_p_s_p_C] = 1 m/s/K metric | velocity change with temperature | meter per second per degree Celsius" + "\n" +
"[Ft_p_s_p_degF] = 0.54864 [m_p_s_p_K] nonmetric | velocity change with temperature | foot per second per degree Fahrenheit" + "\n" +
"[ozf_av] = 1 [oz_av].[g] nonmetric | force | ounce force (av)" + "\n" +
"[klbf_av] = 1000 [lb_av].[g] nonmetric | force | kilo pound force (av)" + "\n" +
"kN = 1 kN metric | force | kiloNewton" + "\n" +
"[rev] = 6.28319 rad nonmetric | angle | revolution" + "\n" +
"[sign] = 0.523599 rad nonmetric | angle | sign" + "\n" +
"cent = 0.01 $ nonmetric | currency | cent" + "\n" +
"[JPY] = 0.00906 $ nonmetric | currency | yen" + "\n" +
"[SEK] = 0.1328 $ nonmetric | currency | Swedish Krona" + "\n" +
"[CHF] = 0.7835 $ nonmetric | currency | Swiss Franc" + "\n" +
"[g_p_cm3] = 1 g/cm3 metric | density | gram per cubic centimeter" + "\n" +
"[g_p_m3] = 1 g/m3 metric | density | gram per cubic meter" + "\n" +
"[g_p_l] = 1 g/l metric | density | gram per liter" + "\n" +
"[kg_p_dm3] = 1000 kg/m3 metric | density | kilogram per cubic decimeter" + "\n" +
"[kg_p_m3] = 1 kg/m3 metric | density | kilogram per cubic meter" + "\n" +
"[kg_p_l] = 1 kg/l metric | density | kilogram per liter" + "\n" +
"[oz_p_ft3] = 1 [oz_av]/[ft_i]3 nonmetric | density | ounce (av.) per cubic foot (Int'l)" + "\n" +
"[oz_p_in3] = 1 [oz_av]/[in_i]3 nonmetric | density | ounce (av.) per cubic inch (Int'l)" + "\n" +
"[lb_p_ft3] = 1 [lb_av]/[ft_i]3 nonmetric | density | pound (av.) per cubic foot (Int'l)" + "\n" +
"[lb_p_in3] = 1 [lb_av]/[in_i]3 nonmetric | density | pound (av.) per cubic inch (Int'l)" + "\n" +
"[lb_p_yd3] = 1 [lb_av]/[yd_i]3 nonmetric | density | pound (av.) per cubic yard (Int'l)" + "\n" +
"[lb_p_galUK] = 1 [lb_av]/[gal_br] nonmetric | density | pound (av.) per gallon (Brit.)" + "\n" +
"[lb_p_galUS] = 1 [lb_av]/[gal_us] nonmetric | density | pound (av.) per gallon liquid (U.S.)" + "\n" +
"[sl_p_ft3] = 1 slug/[ft_i]3 nonmetric | density | slug per cubic foot (Int'l)" + "\n" +
"[sl_p_in3] = 1 slug/[in_i]3 nonmetric | density | slug per cubic inch (Int'l)" + "\n" +
"[sl_p_yd3] = 1 slug/[yd_i]3 nonmetric | density | slug per cubic yard (Int'l)" + "\n" +
"[sGrad] = 1000 kg/m3 metric | density | specific gravity" + "\n" +
"[btuIT_p_h_p_ft2] = 1 [Btu_IT]/h/[ft_i]2 nonmetric | energy flux | Btu (intl. table) per hour per square foot (Int'l)" + "\n" +
"[btuTH_p_h_p_ft2] = 1 [Btu_th]/h/[ft_i]2 nonmetric | energy flux | Btu (thermochemical) per hour per square foot (Int'l)" + "\n" +
"[lang] = 697.5 W/m2 nonmetric | energy flux | langley (flux)" + "\n" +
"[w_p_m2] = 1 W/m2 metric | energy flux | watt per square meter" + "\n" +
"[kw_p_m2] = 1 kW/m2 metric | energy flux | kilowatt per square meter" + "\n" +
"[kw-h_p_m2-yr] = 1 kW.h/m2/a nonmetric | energy flux | kilowatt-hour per square meter per year" + "\n" +
"[MJ_p_m2-yr] = 1 MJ/m2/a nonmetric | energy flux | megaJoule per square meter per year" + "\n" +
"[w_p_m] = 1 W/m metric | power per length | watt per meter" + "\n" +
"[kw_p_m] = 1 kW/m metric | power per length | kilowatt per meter" + "\n" +
"[W_p_m2_p_K] = 1 W/m2/K metric | heat transfer coefficient | watt per square meter per kelvin" + "\n" +
"[BtuIT_p_h_p_ft2_p_degF] = 5.67826 [W_p_m2_p_K] nonmetric | heat transfer coefficient | Btu (intl. table) per square foot per hour per Fahrenheit degree" + "\n" +
"[BtuTH_p_h_p_ft2_p_degF] = 5.67446 [W_p_m2_p_K] nonmetric | heat transfer coefficient | Btu (thermochemical) per square foot per hour per Fahrenheit degree" + "\n" +
"[W_p_cm2_p_K] = 1 W/cm2/K metric | heat transfer coefficient | watt per square centimeter per kelvin" + "\n" +
"[W_p_m2_p_Cel] = 1 W/m2/K metric | heat transfer coefficient | watt per square meter per degree celcius" + "\n" +
"[$_p_cm3] = 1 $/cm3 nonmetric | cost density | dollar per cubic centimeter" + "\n" +
"[$_p_m2] = 1 $/m2 nonmetric | cost density | dollar per square meter" + "\n" +
"[$_p_kW_p_h] = 1 $/kW/h nonmetric | cost density | dollar per kilowatt-hour" + "\n" +
"[cent_p_kW_p_h] = 0.01 $/kW/h nonmetric | cost density | cent per kilowatt-hour" + "\n" +
"[$_p_W_p_h] = 1 $/W/h nonmetric | cost density | dollar per watt-hour" + "\n" +
"[$_p_kW] = 1 $/kW nonmetric | cost density | dollar per kilowatt" + "\n" +
"[$_p_W] = 1 $/W nonmetric | cost density | dollar per watt" + "\n" +
"[$_p_l] = 1 $/l nonmetric | cost density | dollar per liter" + "\n" +
"[Ft_lb] = 1 [ft_i].[lb_av] nonmetric | moment | foot pound (Int'l)(av)" + "\n" +
"[N-m] = 1 N.m metric | torque | newton-meter" + "\n" +
"[dyn-cm] = 1 dyn.cm metric | torque | dyne-centimeter" + "\n" +
"[gf-cm] = 1 gf.cm metric | torque | gram force-centimeter" + "\n" +
"[gf-mm] = 1 gf.mm metric | torque | gram force-millimeter" + "\n" +
"[kgf-m] = 1 kgf.m metric | torque | kilogram force-meter" + "\n" +
"[N-cm] = 1 N.cm metric | torque | newton-centimeter" + "\n" +
"[ozf-in] = 1 [ozf_av].[in_i] nonmetric | torque | ounce force-inch" + "\n" +
"[lbf-ft] = 1 [lbf_av].[ft_i] nonmetric | torque | pound force-foot" + "\n" +
"[lbf-in] = 1 [lbf_av].[in_i] nonmetric | torque | pound force-inch" + "\n" +
"[pdl-ft] = 0.0421401 [N-m] nonmetric | torque | poundal-foot" + "\n" +
"kW = 1 kW metric | power | kilowatt" + "\n" +
"MW = 1 MW metric | power | megawatt" + "\n" +
"GW = 1 GW metric | power | gigawatt" + "\n" +
"TW = 1 TW metric | power | terawatt" + "\n" +
"[min_p_deg] = 1 min/deg nonmetric | time to angle rate | minute per degree" + "\n" +
"[h_p_deg] = 1 h/deg nonmetric | time to angle rate | hour per degree" + "\n" +
"[N-s_p_mm] = 1 N.s/mm metric | dynamic viscosity | Newton second per millimeter" + "\n" +
"[N-s_p_100mm] = 0.01 N.s/mm metric | dynamic viscosity | Newton second per 100 millimeter" + "\n" +
"[Pa-s] = 1 Pa.s metric | dynamic viscosity | pascal second" + "\n" +
"cP = 1 cP metric | dynamic viscosity | centiPoise" + "\n" +
"[dyn-s_p_cm2] = 0.1 [Pa-s] metric | dynamic viscosity | dyne second per square centimeter" + "\n" +
"[N-s_p_m2] = 1 [Pa-s] metric | dynamic viscosity | Newton second per square meter" + "\n" +
"[gf-s_p_cm2] = 98.0665 [Pa-s] metric | dynamic viscosity | gram force second per square centimeter" + "\n" +
"[g_p_cm-s] = 0.1 [Pa-s] metric | dynamic viscosity | gram per centimeter per second" + "\n" +
"[kgf-s_p_m2] = 9.80665 [Pa-s] metric | dynamic viscosity | kilogram force second per square meter" + "\n" +
"[kg_p_m-s] = 1 [Pa-s] metric | dynamic viscosity | kilogram per meter per second" + "\n" +
"[Pl] = 1 [Pa-s] metric | dynamic viscosity | poiseuille" + "\n" +
"[lbf-s_p_ft2] = 47.8803 [Pa-s] nonmetric | dynamic viscosity | pound force second per square foot" + "\n" +
"[lbm_p_ft-s] = 1.48816 [Pa-s] nonmetric | dynamic viscosity | pound mass per foot second" + "\n" +
"[lbm_p_in-s] = 17.858 [Pa-s] nonmetric | dynamic viscosity | pound mass per inch second" + "\n" +
"[reyn] = 6894.76 [Pa-s] nonmetric | dynamic viscosity | reynolds (reyns)" + "\n" +
"[sl_p_ft-s] = 47.8803 [Pa-s] nonmetric | dynamic viscosity | slug per foot second" + "\n" +
"[sl_p_in-s] = 574.563 [Pa-s] nonmetric | dynamic viscosity | slug per inch second" + "\n" +
"[kg-m2] = 1 kg.m2 metric | mass moment of inertia | kilogram-square meter" + "\n" +
"[kg-mm2] = 1 kg.mm2 metric | mass moment of inertia | kilogram-square millimeter" + "\n" +
"[g-cm2] = 1 g.cm2 metric | mass moment of inertia | gram-square centimeter" + "\n" +
"[lbm-ft2] = 4.21401E-2 [kg-m2] nonmetric | mass moment of inertia | pound mass-square foot" + "\n" +
"[lbm-in2] = 2.9264E-4 [kg-m2] nonmetric | mass moment of inertia | pound mass-square inch" + "\n" +
"[sl-ft2] = 1.35582 [kg-m2] nonmetric | mass moment of inertia | slug-square foot" + "\n" +
"[sl-in2] = 9.4154E-3 [kg-m2] nonmetric | mass moment of inertia | slug-square inch" + "\n" +
"[lbf-ft_p_deg] = 1 [lbf-ft]/deg nonmetric | | pound force-foot per degree" + "\n" +
"[g_p_100mm] = 10 g/m metric | | gram per 100 millimeter" + "\n" +
"[g_p_100] = 0.01 g metric | | gram per 100" + "\n" +
"[g-mm_p_100] = 0.01 g.mm metric | | gram millimeter per 100" + "\n" +
"[N-s-mm_p_100] = 1 N.s.mm/100 metric | | Newton second millimeter per 100" + "\n" +
"[N-s-mm2_p_100] = 1 N.s.mm2/100 metric | | Newton second square millimeter per 100" + "\n" +
"[J_p_kg_p_K] = 1 J/kg/K metric | specific heat | Joule per kilogram per kelvin" + "\n" +
"[BtuIT_p_lb_p_degF] = 4186.8 [J_p_kg_p_K] nonmetric | specific heat | Btu. (IT) per pound per degree Fahrenheit" + "\n" +
"[BtuTH_p_lb_p_degF] = 4184 [J_p_kg_p_K] nonmetric | specific heat | Btu. (TH) per pound per degree Fahrenheit" + "\n" +
"[CalIT_p_g_p_degC] = 4186.74 [J_p_kg_p_K] metric | specific heat | Calorie (IT) per gram per degree Celsius" + "\n" +
"[mayer] = 1000 [J_p_kg_p_K] nonmetric | specific heat | mayer" + "\n" +
"[N-m_p_kg_p_K] = 1 [J_p_kg_p_K] metric | specific heat | Newton meter per kilogram per kelvin" + "\n" +
"[lbf-ft_p_lb_p_degF] = 5.38033 [J_p_kg_p_K] nonmetric | specific heat | Pound force foot per pound mass per degree Fahrenheit" + "\n" +
"[m2_p_V] = 1 m2/V nonmetric | | square meter per Volt" + "\n" +
"[K-1] = 1 K-1 nonmetric | | per Kelvin" + "\n" +
"[A_p_K3] = 1 A/K3 nonmetric | | Amp`ere per cubic Kelvin" + "\n" +
"[J_p_K] = 1 J/K nonmetric | | Joule per Kelvin" + "\n" +
"[p_h] = 1 h-1 nonmetric | frequency | per hour" + "\n" +
"" + "\n" +
"[W_p_m_p_K] = 1 W/m/K metric | thermal conductivity | watt per meter per kelvin" + "\n" +
"[BtuIT_p_f_p_h_p_degF] = 1.73073 [W_p_m_p_K] nonmetric | thermal conductivity | Btu. (IT) per foot per hour per degree Fahrenheit" + "\n" +
"[BtuIT-in_p_f2_p_h_p_degF] = 0.144228 [W_p_m_p_K] nonmetric | thermal conductivity | Btu. (IT)-inch per square foot per hour per degree Fahrenheit" + "\n" +
"[BtuIT-in_p_f2_p_s_p_degF] = 519.22 [W_p_m_p_K] nonmetric | thermal conductivity | Btu. (IT)-inch per square foot per second per degree Fahrenheit" + "\n" +
"[BtuTH_p_f_p_h_p_degF] = 1.72958 [W_p_m_p_K] nonmetric | thermal conductivity | Btu. (TH) per foot per hour per degree Fahrenheit" + "\n" +
"[BtuTH-in_p_f2_p_h_p_degF] = 0.144131 [W_p_m_p_K] nonmetric | thermal conductivity | Btu. (TH)-inch per square foot per hour per degree Fahrenheit" + "\n" +
"[BtuTH-in_p_f2_p_s_p_degF] = 518.873 [W_p_m_p_K] nonmetric | thermal conductivity | Btu. (TH)-inch per square foot per second per degree Fahrenheit" + "\n" +
"[CalIT_p_cm_p_s_p_degC] = 418.673 [W_p_m_p_K] metric | thermal conductivity | Calorie (IT) per centimeter per second per degree Celsius" + "\n" +
"[CalTH_p_cm_p_s_p_degC] = 418.4 [W_p_m_p_K] metric | thermal conductivity | Calorie (TH) per centimeter per second per degree Celsius" + "\n" +
"[W_p_cm_p_degC] = 100 [W_p_m_p_K] metric | thermal conductivity | watt per centimeter per degree Celsius" + "\n" +
"[W_p_m_p_degC] = 1 [W_p_m_p_K] metric | thermal conductivity | watt per meter per degree Celsius" + "\n" +
"" + "\n" +
"[W-hr_p_s] = 1 W.h/s nonmetric | power | watt-hour per second" + "\n" +
"[W-hr_p_d] = 1 W.h/d nonmetric | power | watt-hour per day" + "\n" +
"[W-hr_p_a] = 1 W.h/a nonmetric | power | watt-hour per year" + "\n" +
"[kW-hr_p_s] = 1 kW.h/s nonmetric | power | kilowatt-hour per second" + "\n" +
"[kW-hr_p_d] = 1 kW.h/d nonmetric | power | kilowatt-hour per day" + "\n" +
"[kW-hr_p_a] = 1 kW.h/a nonmetric | power | kilowatt-hour per year" + "\n" +
"[MW-hr_p_s] = 1 MW.h/s nonmetric | power | megawatt-hour per second" + "\n" +
"[MW-hr_p_d] = 1 MW.h/d nonmetric | power | megawatt-hour per day" + "\n" +
"[MW-hr_p_a] = 1 MW.h/a nonmetric | power | megawatt-hour per year" + "\n" +
"[GW-hr_p_s] = 1 GW.h/s nonmetric | power | gigawatt-hour per second" + "\n" +
"[GW-hr_p_d] = 1 GW.h/d nonmetric | power | gigawatt-hour per day" + "\n" +
"[GW-hr_p_a] = 1 GW.h/a nonmetric | power | gigawatt-hour per year" + "\n" +
"[TW-hr_p_s] = 1 TW.h/s nonmetric | power | terawatt-hour per second" + "\n" +
"[TW-hr_p_d] = 1 TW.h/d nonmetric | power | terawatt-hour per day" + "\n" +
"[TW-hr_p_a] = 1 TW.h/a nonmetric | power | terawatt-hour per year" + "\n" +
"[J_p_h] = 1 J/h nonmetric | power | Joule per hour" + "\n" +
"[J_p_d] = 1 J/d nonmetric | power | Joule per day" + "\n" +
"[J_p_a] = 1 J/a nonmetric | power | Joule per year" + "\n" +
"[kJ_p_h] = 1 kJ/h nonmetric | power | kiloJoule per hour" + "\n" +
"[kJ_p_d] = 1 kJ/d nonmetric | power | kiloJoule per day" + "\n" +
"[kJ_p_a] = 1 kJ/a nonmetric | power | kiloJoule per year" + "\n" +
"[MJ_p_h] = 1 MJ/h nonmetric | power | megaJoule per hour" + "\n" +
"[MJ_p_d] = 1 MJ/d nonmetric | power | megaJoule per day" + "\n" +
"[MJ_p_yr] = 1 MJ/a nonmetric | power | megaJoule per year" + "\n" +
"[GJ_p_h] = 1 GJ/h nonmetric | power | gigaJoule per hour" + "\n" +
"[GJ_p_d] = 1 GJ/d nonmetric | power | gigaJoule per day" + "\n" +
"[GJ_p_a] = 1 GJ/a nonmetric | power | gigaJoule per year" + "\n" +
"[BtuIT_p_h] = 0.293071 W nonmetric | power (heat flow) | Btu (intl. table) per hour" + "\n" +
"[BtuIT_p_m] = 17.5843 W nonmetric | power (heat flow) | Btu (intl. table) per minute" + "\n" +
"[BtuIT_p_s] = 1055.06 W nonmetric | power (heat flow) | Btu (intl. table) per second" + "\n" +
"[BtuTH_p_h] = 0.292875 W nonmetric | power (heat flow) | Btu (therm.) per hour" + "\n" +
"[BtuTH_p_m] = 17.5725 W nonmetric | power (heat flow) | Btu (therm.) per minute" + "\n" +
"[BtuTH_p_s] = 1054.35 W nonmetric | power (heat flow) | Btu (therm.) per second" + "\n" +
"" + "\n" +
"[btu_p_ft2] = 1 [Btu_IT]/[ft_i]2 nonmetric | energy density (per area) | Btu (intl. table) per square foot (Int'l)" + "\n" +
"[j_p_m2] = 1 J/m2 metric | energy density (per area) | Joule per square meter" + "\n" +
"[kj_p_m2] = 1 kJ/m2 metric | energy density (per area) | kiloJoule per square meter" + "\n" +
"[Mj_p_m2] = 1 MJ/m2 metric | energy density (per area) | megaJoule per square meter" + "\n" +
"[Gj_p_m2] = 1 GJ/m2 metric | energy density (per area) | gigaJoule per square meter" + "\n" +
"[W-hr_p_m2] = 1 W.h/m2 nonmetric | energy density (per area) | watt-hour per square meter" + "\n" +
"[kW-hr_p_m2] = 1 kW.h/m2 nonmetric | energy density (per area) | kilowatt-hour per square meter" + "\n" +
"[MW-hr_p_m2] = 1 MW.h/m2 nonmetric | energy density (per area) | megawatt-hour per square meter" + "\n" +
"[GW-hr_p_m2] = 1 GW.h/m2 nonmetric | energy density (per area) | gigawatt-hour per square meter" + "\n" +
"[TW-hr_p_m2] = 1 TW.h/m2 nonmetric | energy density (per area) | terawatt-hour per square meter" + "\n" +
"" + "\n" +
"[btu_p_lb] = 1 [Btu_IT]/[lb_av] nonmetric | thermal heat capacity | Btu (intl. table) per pound (av.)" + "\n" +
"[j_p_kg] = 1 J/kg metric | thermal heat capacity | Joule per kilogram" + "\n" +
"[j_p_t] = 1 J/t nonmetric | thermal heat capacity | Joule per tonne" + "\n" +
"[kj_p_kg] = 1 kJ/kg metric | thermal heat capacity | kiloJoule per kilogram" + "\n" +
"[kj_p_t] = 1 kJ/t nonmetric | thermal heat capacity | kiloJoule per tonne" + "\n" +
"[Mj_p_kg] = 1 MJ/kg metric | thermal heat capacity | megaJoule per kilogram" + "\n" +
"[Mj_p_t] = 1 MJ/t metric | thermal heat capacity | megaJoule per tonne" + "\n" +
"[Gj_p_kg] = 1 GJ/kg metric | thermal heat capacity | gigaJoule per kilogram" + "\n" +
"[Gj_p_t] = 1 GJ/t metric | thermal heat capacity | gigaJoule per tonne" + "\n" +
"" + "\n" +
"[W-hr_p_kg] = 1 W.h/kg nonmetric | thermal heat capacity | watt-hour per kilogram" + "\n" +
"[W-hr_p_t] = 1 W.h/t nonmetric | thermal heat capacity | watt-hour per tonne" + "\n" +
"[kW-hr_p_kg] = 1 kW.h/kg nonmetric | thermal heat capacity | kilowatt-hour per kilogram" + "\n" +
"[kW-hr_p_t] = 1 kW.h/t nonmetric | thermal heat capacity | kilowatt-hour per tonne" + "\n" +
"[MW-hr_p_kg] = 1 MW.h/kg nonmetric | thermal heat capacity | megawatt-hour per kilogram" + "\n" +
"[MW-hr_p_t] = 1 MW.h/t nonmetric | thermal heat capacity | megawatt-hour per tonne" + "\n" +
"[GW-hr_p_kg] = 1 GW.h/kg nonmetric | thermal heat capacity | gigawatt-hour per kilogram" + "\n" +
"[GW-hr_p_t] = 1 GW.h/t nonmetric | thermal heat capacity | gigawatt-hour per tonne" + "\n" +
"[TW-hr_p_kg] = 1 TW.h/kg nonmetric | thermal heat capacity | terawatt-hour per kilogram" + "\n" +
"[TW-hr_p_t] = 1 TW.h/t nonmetric | thermal heat capacity | terawatt-hour per tonne" + "\n" +
"" + "\n" +
"[W-hr_p_W] = 1 h nonmetric | energy input per production capacity | watt-hour per watt" + "\n" +
"[kW-hr_p_W] = 1E3 h nonmetric | energy input per production capacity | kilowatt-hour per watt" + "\n" +
"[MW-hr_p_W] = 1E6 h nonmetric | energy input per production capacity | megawatt-hour per watt" + "\n" +
"[GW-hr_p_W] = 1E9 h nonmetric | energy input per production capacity | gigawatt-hour per watt" + "\n" +
"[TW-hr_p_W] = 1E12 h nonmetric | energy input per production capacity | terawatt-hour per watt" + "\n" +
"[J_p_W] = 1 J/W nonmetric | energy input per production capacity | Joule per watt" + "\n" +
"[kJ_p_W] = 1E3 J/W nonmetric | energy input per production capacity | kiloJoule per watt" + "\n" +
"[MJ_p_W] = 1E6 J/W nonmetric | energy input per production capacity | megaJoule per watt" + "\n" +
"[GJ_p_W] = 1E9 J/W nonmetric | energy input per production capacity | gigaJoule per watt" + "\n" +
"" + "\n" +
"[btuIT_p_ft3] = 1 [Btu_IT]/[ft_i]3 nonmetric | energy density (per volume) | Btu (intl. table) per cubic foot (Int'l)" + "\n" +
"[btuIT_p_galUK] = 1 [Btu_IT]/[gal_br] nonmetric | energy density (per volume) | Btu (intl. table) per gallon (Brit.)" + "\n" +
"[btuIT_p_galUS] = 1 [Btu_IT]/[gal_us] nonmetric | energy density (per volume) | Btu (intl. table) per gallon (U.S.)" + "\n" +
"[j_p_m3] = 1 J/m3 metric | energy density (per volume) | Joule per cubic meter" + "\n" +
"[j_p_l] = 1 J/l metric | energy density (per volume) | Joule per liter" + "\n" +
"[kj_p_m3] = 1 kJ/m3 metric | energy density (per volume) | kiloJoule per cubic meter" + "\n" +
"[kj_p_l] = 1 kJ/l metric | energy density (per volume) | kiloJoule per liter" + "\n" +
"[Mj_p_m3] = 1 MJ/m3 metric | energy density (per volume) | megaJoule per cubic meter" + "\n" +
"[Mj_p_l] = 1 MJ/l metric | energy density (per volume) | megaJoule per liter" + "\n" +
"[Gj_p_m3] = 1 GJ/m3 metric | energy density (per volume) | gigaJoule per cubic meter" + "\n" +
"[Gj_p_l] = 1 GJ/l metric | energy density (per volume) | gigaJoule per liter" + "\n" +
"[MGOe] = 7957.75 J/m3 nonmetric | energy density (per volume) | megagauss-oersted" + "\n" +
"[Wh_p_l] = 1 W.h/l nonmetric | energy density (per volume) | watt-hour per liter" + "\n" +
"[Wh_p_m3] = 1 W.h/m3 nonmetric | energy density (per volume) | watt-hour per cubic meter" + "\n" +
"[kWh_p_l] = 1 kW.h/l nonmetric | energy density (per volume) | kilowatt-hour per liter" + "\n" +
"[kWh_p_m3] = 1 kW.h/m3 nonmetric | energy density (per volume) | kilowatt-hour per cubic meter" + "\n" +
"[MWh_p_l] = 1 MW.h/l nonmetric | energy density (per volume) | megawatt-hour per liter" + "\n" +
"[MWh_p_m3] = 1 MW.h/m3 nonmetric | energy density (per volume) | megawatt-hour per cubic meter" + "\n" +
"[GWh_p_l] = 1 MW.h/l nonmetric | energy density (per volume) | gigawatt-hour per liter" + "\n" +
"[GWh_p_m3] = 1 MW.h/m3 nonmetric | energy density (per volume) | gigawatt-hour per cubic meter" + "\n" +
"[TWh_p_l] = 1 TW.h/l nonmetric | energy density (per volume) | terawatt-hour per liter" + "\n" +
"[TWh_p_m3] = 1 TW.h/m3 nonmetric | energy density (per volume) | terawatt-hour per cubic meter" + "\n" +
"[Ws_p_mm3] = 1 W.s/m3 nonmetric |energy density (per volume) |  watt-second per cubic millimeter" + "\n" +
"[hp_p_in3] = 1 [HP].s/[cin_i] nonmetric |energy density (per volume) |  horsepower-second per cubic inch" + "\n" +
"" + "\n" +
"[g_p_s] = 1 g/s metric | mass flow rate | gram per second" + "\n" +
"[g_p_h] = 1 g/h nonmetric | mass flow rate | gram per hour" + "\n" +
"[g_p_d] = 1 g/d nonmetric | mass flow rate | gram per day" + "\n" +
"[g_p_a] = 1 g/a nonmetric | mass flow rate | gram per year" + "\n" +
"[kg_p_s] = 1 kg/s metric | mass flow rate | kilogram per second" + "\n" +
"[kg_p_h] = 1 kg/h nonmetric | mass flow rate | kilogram per hour" + "\n" +
"[kg_p_d] = 1 kg/d nonmetric | mass flow rate | kilogram per day" + "\n" +
"[kg_p_a] = 1 kg/a nonmetric | mass flow rate | kilogram per year" + "\n" +
"[t_p_s] = 1 t/s metric | mass flow rate | tonne per second" + "\n" +
"[t_p_h] = 1 t/h nonmetric | mass flow rate | tonne per hour" + "\n" +
"[t_p_d] = 1 t/d nonmetric | mass flow rate | tonne per day" + "\n" +
"[t_p_a] = 1 t/a nonmetric | mass flow rate | tonne per year" + "\n" +
"[kt_p_s] = 1 kt/s metric | mass flow rate | kilotonne per second" + "\n" +
"[kt_p_h] = 1 kt/h nonmetric | mass flow rate | kilotonne per hour" + "\n" +
"[kt_p_d] = 1 kt/d nonmetric | mass flow rate | kilotonne per day" + "\n" +
"[kt_p_a] = 1 kt/a nonmetric | mass flow rate | kilotonne per year" + "\n" +
"" + "\n" +
"[g_p_m2] = 1 g/m2 metric | surface density | gram per square meter" + "\n" +
"[kg_p_m2] = 1 kg/m2 metric | surface density | kilogram per square meter" + "\n" +
"[t_p_m2] = 1 t/m2 metric | surface density | tonne per square meter" + "\n" +
"[kt_p_m2] = 1 kt/m2 metric | surface density | kilotonne per square meter" + "\n" +
"" + "\n" +
"[t_p_kW_a] = 1 t/kW/a nonmetric | emission rate | tonne per kilowatt-year" + "\n" +
"[t_p_kWh_p_yr] = 1 t/kW/h/a nonmetric | emission rate | tonne per kilowatt-hour-year" + "\n" +
"[g_p_W] = 1 g/W metric | emission rate | gram per watt" + "\n" +
"[kg_p_W] = 1 kg/W metric | emission rate | kilogram per watt" + "\n" +
"[t_p_W] = 1 t/W metric | emission rate | tonne per watt" + "\n" +
"[kt_p_W] = 1 kt/W metric | emission rate | kilotonne per watt" + "\n" +
"[g_p_Wh] = 1 g/W/h nonmetric | emission rate | gram per watt-hour" + "\n" +
"[kg_p_Wh] = 1 kg/W/h nonmetric | emission rate | kilogram per watt-hour" + "\n" +
"[t_p_Wh] = 1 t/W/h nonmetric | emission rate | tonne per watt-hour" + "\n" +
"[kt_p_Wh] = 1 kt/W/h nonmetric | emission rate | kilotonne per watt-hour" + "\n" +
"[g_p_J] = 1 g/J metric | emission rate | gram per Joule" + "\n" +
"[kg_p_J] = 1 kg/J metric | emission rate | kilogram per Joule" + "\n" +
"[t_p_J] = 1 t/J metric | emission rate | tonne per Joule" + "\n" +
"[kt_p_J] = 1 kt/J metric | emission rate | kilotonne per Joule" + "\n" +
"[lb_p_bBTU] = 1E-9 [lb_av]/[Btu_th] nonmetric | emission rate | pound per billion BTU" + "\n" +
"[kg_p_GWh] = 1E-9 kg/W/h metric | emission rate | kilogram per gigawatt-hour" + "\n" +
"[lb_p_MWh] = 1E-6 [lb_av]/W/h nonmetric | emission rate | pound per megawatt-hour" + "\n" +
"[lb_p_Mscf] = 1E-6 [lb_av]/[ft_i]3 nonmetric | emission rate | pound per million cubic foot" + "\n" +
"" + "\n" +
"[t_p_m3] = 1 t/m3 metric | density | tonne per cubic meter" + "\n" +
"[t_p_l] = 1 t/l metric | density | tonne per liter" + "\n" +
"[kt_p_m3] = 1 kt/m3 metric | density | kilotonne per cubic meter" + "\n" +
"[kt_p_l] = 1 kt/l metric | density | kilotonne per liter" + "\n" +
"" + "\n" +
"[l_p_s] = 1 l/s metric | volumetric flow rate | liter per second" + "\n" +
"[l_p_h] = 1 l/h nonmetric | volumetric flow rate | liter per hour" + "\n" +
"[l_p_d] = 1 l/d nonmetric | volumetric flow rate | liter per day" + "\n" +
"[l_p_a] = 1 l/a nonmetric | volumetric flow rate | liter per year" + "\n" +
"[ml_p_s] = 1 ml/s metric | volumetric flow rate | liter per second" + "\n" +
"[ml_p_h] = 1 ml/h nonmetric | volumetric flow rate | milliliter per hour" + "\n" +
"[ml_p_d] = 1 ml/d nonmetric | volumetric flow rate | milliliter per day" + "\n" +
"[ml_p_a] = 1 ml/a nonmetric | volumetric flow rate | milliliter per year" + "\n" +
"[m3_p_s] = 1 m3/s metric | volumetric flow rate | cubic meter per second" + "\n" +
"[m3_p_h] = 1 m3/h nonmetric | volumetric flow rate | cubic meter per hour" + "\n" +
"[m3_p_d] = 1 m3/d nonmetric | volumetric flow rate | cubic meter per day" + "\n" +
"[m3_p_a] = 1 m3/a nonmetric | volumetric flow rate | cubic meter per year" + "\n" +
"[ft3_p_s] = 1 [cft_i]/s nonmetric | volumetric flow rate | cubic feet per second" + "\n" +
"[ft3_p_min] = 1 [cft_i]/min nonmetric | volumetric flow rate | cubic feet per minute" + "\n" +
"[galUS_p_s] = 3.78541E-3 m3/s nonmetric | volumetric flow rate | gallon (U.S., liq.) per second" + "\n" +
"[galUS_p_m] = 6.30902E-5 m3/s nonmetric | volumetric flow rate | gallon (U.S., liq.) per minute" + "\n" +
"[galUS_p_h] = 1.0515E-6 m3/s nonmetric | volumetric flow rate | gallon (U.S., liq.) per hour" + "\n" +
"[galUS_p_d] = 4.38126E-8 m3/s nonmetric | volumetric flow rate | gallon (U.S., liq.) per day" + "\n" +
"" + "\n" +
"[l_p_m2] = 1 l/m2 nonmetric | length | liter per square meter" + "\n" +
"[ml_p_m2] = 1 ml/m2 nonmetric | length | milliliter per square meter" + "\n" +
"" + "\n" +
"[l_p_kW_p_h] = 1 l/kW/h nonmetric | fuel consumption rate | liter per kilowatt-hour" + "\n" +
"[l_p_W_p_h] = 1 l/W/h nonmetric | fuel consumption rate | liter per watt-hour" + "\n" +
"[ml_p_W_p_h] = 1 ml/W/h nonmetric | fuel consumption rate | milliliter per watt-hour" + "\n" +
"[m3_p_W_p_h] = 1 m3/W/h nonmetric | fuel consumption rate | cubic meter per watt-hour" + "\n" +
"[l_p_J] = 1 l/J metric | fuel consumption rate | liter per Joule" + "\n" +
"[ml_p_J] = 1 ml/J metric | fuel consumption rate | milliliter per Joule" + "\n" +
"[m3_p_J] = 1 m3/J metric | fuel consumption rate | cubic meter per Joule" + "\n" +
"" + "\n" +
"[mi_p_gal] = 1 [mi_us]/[gal_us] nonmetric | fuel consumption rate | mile per gallon (U.S.)" + "\n" +
"" + "\n" +
"[l_p_W] = 1 l/W metric | per power capacity | liter per watt" + "\n" +
"[ml_p_W] = 1 ml/W metric | per power capacity | milliliter watt" + "\n" +
"[m3_p_W] = 1 m3/W metric | per power capacity | cubic meter per watt" + "\n" +
"" + "\n" +
"[l_p_kg] = 1 l/kg metric | specific volume | liter per kilogram" + "\n" +
"[ml_p_kg] = 1 ml/kg metric | specific volume | milliliter per kilogram" + "\n" +
"[m3_p_kg] = 1 m3/kg metric | specific volume | cubic meter per kilogram" + "\n" +
"[l_p_t] = 1 l/t metric | specific volume | liter per tonne" + "\n" +
"[ml_p_t] = 1 ml/t metric | specific volume | milliliter per tonne" + "\n" +
"[m3_p_t] = 1 m3/t metric | specific volume | cubic meter per tonne" + "\n" +
"" + "\n" +
"[cm2_p_s] = 1 cm2/s metric | kinematic viscosity | square centimeter per second" + "\n" +
"[m2_p_s] = 1 m2/s metric | kinematic viscosity | square meter per second" + "\n" +
"[km2_p_s] = 1 km2/s metric | kinematic viscosity | square kilometer per second" + "\n" +
"[cm2_p_h] = 1 cm2/h nonmetric | kinematic viscosity | square centimeter per hour" + "\n" +
"[m2_p_h] = 1 m2/h nonmetric | kinematic viscosity | square meter per hour" + "\n" +
"[km2_p_h] = 1 km2/h nonmetric | kinematic viscosity | square kilometer per hour" + "\n" +
"[cm2_p_d] = 1 cm2/d nonmetric | kinematic viscosity | square centimeter per day" + "\n" +
"[m2_p_d] = 1 m2/d nonmetric | kinematic viscosity | square meter per day" + "\n" +
"[km2_p_d] = 1 km2/d nonmetric | kinematic viscosity | square kilometer per day" + "\n" +
"[cm2_p_a] = 1 cm2/a nonmetric | kinematic viscosity | square centimeter per year" + "\n" +
"[m2_p_a] = 1 m2/a nonmetric | kinematic viscosity | square meter per year" + "\n" +
"[km2_p_a] = 1 km2/a nonmetric | kinematic viscosity | square kilometer per year" + "\n" +
"" + "\n" +
"[cm2_p_m2] = 1 cm2/m3 metric | per unit material | square centimeter per cubic meter" + "\n" +
"[m2_p_m2] = 1 m2/m3 metric | per u-nit material | square meter per cubic meter" + "\n" +
"[km2_p_m2] = 1 km2/m3 metric | per unit material | square kilometer per cubic meter" + "\n" +
"[cm2_p_W] = 1 cm2/W metric | per power capacity | square centimeter per watt" + "\n" +
"[m2_p_W] = 1 m2/W metric | per power capacity | square meter per watt" + "\n" +
"[km2_p_W] = 1 km2/W metric | per power capacity | square kilometer per watt" + "\n" +
"[cm2_p_W-h] = 1 cm2/W/h nonmetric | per energy production | square centimeter per watt-hour" + "\n" +
"[m2_p_W-h] = 1 m2/W/h nonmetric | per energy production | square meter per watt-hour" + "\n" +
"[km2_p_W-h] = 1 km2/W/h nonmetric | per energy production | square kilometer per watt-hour" + "\n" +
"[cm2_p_J] = 1 cm2/J metric | per energy production | square centimeter per Joule" + "\n" +
"[m2_p_J] = 1 m2/J metric | per energy production | square meter per Joule" + "\n" +
"[km2_p_J] = 1 km2/J metric | per energy production | square kilometer per Joule" + "\n" +
"" + "\n" +
"[cm2_p_kg] = 1 cm2/kg metric | specific surface area | square centimeter per kilogram" + "\n" +
"[m2_p_kg] = 1 m2/kg metric | specific surface area | square meter per kilogram" + "\n" +
"[km2_p_kg] = 1 km2/kg metric | specific surface area | square kilometer per kilogram" + "\n" +
"[cm2_p_t] = 1 cm2/t metric | specific surface area | square centimeter per tonne" + "\n" +
"[m2_p_t] = 1 m2/t metric | specific surface area | square meter per tonne" + "\n" +
"[km2_p_t] = 1 km2/t metric | specific surface area | square kilometer per tonne" + "\n" +
"" + "\n" +
"[yen_p_s] = 1 [JPY]/s nonmetric | cost density | yen per second" + "\n" +
"[yen_p_h] = 1 [JPY]/h nonmetric | cost density | yen per hour" + "\n" +
"[yen_p_d] = 1 [JPY]/d nonmetric | cost density | yen per day" + "\n" +
"[yen_p_a] = 1 [JPY]/a nonmetric | cost density | yen per year" + "\n" +
"[yen_p_l] = 1 [JPY]/l nonmetric | cost density | yen per liter" + "\n" +
"[yen_p_m3] = 1 [JPY]/m3 nonmetric | cost density | yen per cubic meter" + "\n" +
"[yen_p_W] = 1 [JPY]/W nonmetric | cost density | yen per watt" + "\n" +
"[yen_p_W-h] = 1 [JPY]/W/h nonmetric | cost density | yen per watt-hour" + "\n" +
"[yen_p_J] = 1 [JPY]/J nonmetric | cost density | yen per Joule" + "\n" +
"[yen_p_m2] = 1 [JPY]/m2 nonmetric | cost density | yen per square meter" + "\n" +
"[yen_p_kg] = 1 [JPY]/kg nonmetric | cost density | yen per kilogram" + "\n" +
"[yen_p_t] = 1 [JPY]/t nonmetric | cost density | yen per tonne" + "\n" +
"[Myen_p_s] = 1E6 [JPY]/s nonmetric | cost density | megayen per second" + "\n" +
"[Myen_p_h] = 1E6 [JPY]/h nonmetric | cost density | megayenyen per hour" + "\n" +
"[Myen_p_d] = 1E6 [JPY]/d nonmetric | cost density | megayenyen per day" + "\n" +
"[Myen_p_a] = 1E6 [JPY]/a nonmetric | cost density | megayenyen per year" + "\n" +
"[Myen_p_l] = 1E6 [JPY]/l nonmetric | cost density | megayenyen per liter" + "\n" +
"[Myen_p_m3] = 1E6 [JPY]/m3 nonmetric | cost density | megayenyen per cubic meter" + "\n" +
"[Myen_p_W] = 1E6 [JPY]/W nonmetric | cost density | megayenyen per watt" + "\n" +
"[Myen_p_W-h] = 1E6 [JPY]/W/h nonmetric | cost density | megayenyen per watt-hour" + "\n" +
"[Myen_p_J] = 1E6 [JPY]/J nonmetric | cost density | megayenyen per Joule" + "\n" +
"[Myen_p_m2] = 1E6 [JPY]/m2 nonmetric | cost density | megayenyen per square meter" + "\n" +
"[Myen_p_kg] = 1E6 [JPY]/kg nonmetric | cost density | megayenyen per kilogram" + "\n" +
"[Myen_p_t] = 1E6 [JPY]/t nonmetric | cost density | megayenyen per tonne" + "\n" +
"" + "\n" +
"[gr_p_lb] = 1 [gr]/[lb_av] nonmetric | humidity ratio | grain per pound of dry air" + "\n" +
"[lb_p_lb] = 1 [lb_av]/[lb_av] nonmetric | humidity ratio | pound per pound of dry air" + "\n" +
"[degF-hr] = 0.55556 K.h nonmetric | | degree Fahrenheit - hour" + "\n" +
"[BtuTH_p_W-h] = 1 [Btu_th]/W nonmetric | | Btu (therm.) per watt-hour" + "\n" +
"[mm_p_rev] = 1 mm/[rev] nonmetric | feed rate | mm feed per revolution" + "\n" +
"[in_p_rev] = 1 [in_i]/[rev] nonmetric | feed rate | inch feed per revolution" + "\n" +
"[ksi] = 6.896 MPa nonmetric | tensile strength | ksi" + "\n" +
"[kg-smm] = 1 kg.[smm] nonmetric | tensile strength | kg square millimeter" + "\n" +
"" + "\n" +
"## WARNING: Do not use the non-ratio units to directly define a new unit." + "\n" +
"## The non-ratio units are Cel, [degF], Np, B, [dB], B[SPL], B[V], B[mV], B[uV], B[W], B[kW], [pH], and bit_s" + "\n" +
"## Instead, use a ratio of an equivalent unit." + "\n" +
"## For example, to define meter/second/degree Fahrenheit, use 9/5 m/s/K instead of 1 m/s/[degF]";

}


