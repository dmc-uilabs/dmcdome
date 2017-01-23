package buildinglcagui;

import mit.cadlab.dome3.swing.Templates;
import mit.cadlab.dome3.network.client.functions.Vectors;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * Name: BuildingLCAGUIConstants
 * User: jacob
 * Date: Aug 13, 2003
 * Time: 3:14:06 AM
 * Copyright (c) 2003 Massachusetts Institute of Technology. All rights reserved.
 */
public class BuildingLCAGUIConstants
{
	public static final String EMPTY_STRING = "";

	// BuildingLCAInputSpecificationPanel constants
	public static final String BUILDING_TYPE = "building type";
    public static final String CONSTRUCTION_TYPE = "construction type";
	public static final String FLOOR_AREA = "floor area";
	public static final String EVALUATION_PERIOD = "evaluation period";
	public static final String BASE_LIFETIME = "base lifetime";
	public static final String MODIFIED_LIFETIME = "modified lifetime";

	public static final String BASE_AIR_EQUIPMENT = "base air circulation equipment";
	public static final String MODIFIED_AIR_EQUIPMENT = "modified air circulation equipment";
	public static final String BASE_LIGHTING = "base lighting";
	public static final String MODIFIED_LIGHTING = "modified lighting";
	public static final String AREA_PER_ROOM_TYPE1 = "area per room type 1";
	public static final String AREA_PER_ROOM_TYPE2 = "area per room type 2";

	public static final String BASE = "base";
	public static final String MODIFIED = "modified";

	public static final String YEAR_UNIT = "yr";
	public static final String KWH_PER_YEAR_UNIT = "kWh/yr";
	public static final String M2_UNIT = "m2";
	public static final String KG_M2_UNIT = "kg/m2";

	// building type combo box
	public static final String OFFICE = "office";
	public static final String APARTMENT_BUILDING = "apartment building";
	public static final String HOUSE = "house";
	public static final String HOSPITAL = "hospital";
	public static final String DEPARTMENT_STORE = "department store";

	public static final String[] buildingTypeOptions = {

		OFFICE,
		APARTMENT_BUILDING,
		HOUSE,
		HOSPITAL,
		DEPARTMENT_STORE
	};

    // construction type combo box
	public static final String WOOD = "wood";
	public static final String SRC = "SRC";
	public static final String RC = "RC";
	public static final String C = "C";
	public static final String CB = "CB";

	public static final String[] constructionTypeOptions = {

		WOOD,
		SRC,
		RC,
		C,
		CB
	};

	// structure component combo box
	public static final String INNER_SHELL = "inner shell";
	public static final String OUTER_SHELL = "outer shell";
	public static final String STRUCTURE = "structure";
	public static final String ENGINEERING = "engineering";

	public static final String[] componentTypeOptions = {

		ENGINEERING,
		STRUCTURE,
		OUTER_SHELL,
		INNER_SHELL
	};

	public static final String EARTH_REMOVAL = "earth removal";
	public static final String PILINGS = "pilings";

	public static final String[] engineeringOptions = {

		EARTH_REMOVAL,
		PILINGS
	};

	public static final DefaultComboBoxModel engineeringOptionsComboBoxModel = new DefaultComboBoxModel(engineeringOptions);

	public static final String EARTH_REMOVAL_UNITS = "earth removal [m3/m2]";

	public static final String[] earthRemovalOptionComponents = {

		EARTH_REMOVAL_UNITS
	};

	public static final DefaultComboBoxModel earthComponentsComboBoxModel = new DefaultComboBoxModel(earthRemovalOptionComponents);

	public static final String PRE_MADE_PILINGS = "pre-made pilings [m3/m2]";
	public static final String PORTLAND_CONCRETE_PILINGS = "portland concrete pilings [m3/m2]";
	public static final String B_TYPE_CONCRETE_PILINGS = "b-type concrete pilings [m3/m2]";

	public static final String[] pilingOptionComponents = {

		PRE_MADE_PILINGS,
		PORTLAND_CONCRETE_PILINGS,
		B_TYPE_CONCRETE_PILINGS
	};

	public static final DefaultComboBoxModel pilingsComponentsComboBoxModel = new DefaultComboBoxModel(pilingOptionComponents);

	public static final String STRUCTURE_COMPONENTS = "structure components";
	public static final String[] structureOptions = {

        STRUCTURE_COMPONENTS
	};

	public static final DefaultComboBoxModel structureOptionsComboBoxModel = new DefaultComboBoxModel(structureOptions);

	public static final String PORTLAND_CONCRETE = "portland concrete [m3/m2]";
	public static final String B_TYPE_CONCRETE = "b-type concrete [m3/m2]";
	public static final String MOLDING_FRAMEWORK = "molding framework";
	public static final String STEEL_FRAME = "steel frame [kg/m2]";
	public static final String REINFORCING_STEEL_BARS = "reinforcing steel bars [kg/m2]";

	public static final String[] structureOptionComponents = {

		PORTLAND_CONCRETE,
		B_TYPE_CONCRETE,
		MOLDING_FRAMEWORK,
		STEEL_FRAME,
		REINFORCING_STEEL_BARS
	};

	public static final DefaultComboBoxModel structureComponentsComboBoxModel = new DefaultComboBoxModel(structureOptionComponents);

	public static final String ROOF = "roof";
	public static final String OUTER_WALL = "outer wall";
	public static final String OUTER_WINDOW = "outer window";

	public static final String[] outerShellOptions = {

		ROOF,
		OUTER_WALL,
		OUTER_WINDOW
	};

	public static final DefaultComboBoxModel outerShellComboBoxModel = new DefaultComboBoxModel(outerShellOptions);

	public static final String WATER_PROOFING_SHEET = "water proofing sheet [m2/m2]";
	public static final String ANCHORING_CONCRETE_PORTLAND = "anchoring concrete portland [m2/m2]";
	public static final String ANCHORING_CONCRETE_B_TYPE = "anchoring concrete b-type [m2/m2]";
	public static final String FOAM_TYPE_INSULATION = "foam type insulation [m2/m2]";
	public static final String GLASS_WOOL_INSULATION = "glass wool insulation [m2/m2]";

	public static final String[] roofOptionComponents = {

		WATER_PROOFING_SHEET,
		ANCHORING_CONCRETE_PORTLAND,
		ANCHORING_CONCRETE_B_TYPE,
		FOAM_TYPE_INSULATION,
		GLASS_WOOL_INSULATION
	};

	public static final DefaultComboBoxModel roofComponentsComboBoxModel = new DefaultComboBoxModel(roofOptionComponents);

	public static final String TILE = "tile [m2/m2]";
	public static final String SAND = "sand [m2/m2]";
	public static final String PORTLAND_CEMENT = "portland cement [m2/m2]";
	public static final String B_CLASS_CEMENT = "b-class cement [m2/m2]";
	public static final String PAINT = "paint [m2/m2]";

	public static final String[] outerWallOptionComponents = {

		TILE,
		SAND,
		PORTLAND_CEMENT,
		B_CLASS_CEMENT,
		PAINT
	};

	public static final DefaultComboBoxModel outerWallComponentsComboBoxModel = new DefaultComboBoxModel(outerWallOptionComponents);

    public static final String GLASS = "glass [m2/m2]";
	public static final String METAL_FITTINGS = "metal fittings [m2/m2]";
	public static final String WOOD_FITTINGS = "wood fittings [m2/m2]";

	public static final String[] outerWindowOptionComponents = {

		GLASS,
		METAL_FITTINGS,
		WOOD_FITTINGS
	};

	public static final DefaultComboBoxModel outerWindowComponentsComboBoxModel = new DefaultComboBoxModel(outerWindowOptionComponents);

    public static final String FLOORING_MATERIAL = "flooring material";
	public static final String INNER_WALL = "inner wall";
	public static final String INNER_WINDOW = "inner window";
	public static final String CEILING = "ceiling";
	public static final String INSULATION = "insulation";

	public static final String[] innerShellOptions = {

		FLOORING_MATERIAL,
		INNER_WALL,
		INNER_WINDOW,
		CEILING,
		INSULATION
	};

	public static final DefaultComboBoxModel innerShellComboBoxModel = new DefaultComboBoxModel(innerShellOptions);

	public static final String FLOORING = "flooring [m2/m2]";
	public static final String CARPET = "carpet [m2/m2]";
	public static final String VINYL_SHEET = "vinyl sheet [m2/m2]";

	public static final String[] flooringMaterialOptionComponents = {

		FLOORING,
		CARPET,
		VINYL_SHEET,
		TILE,
		PAINT,
		PORTLAND_CEMENT,
		B_CLASS_CEMENT,
		SAND
	};

	public static final DefaultComboBoxModel flooringMaterialComponentsComboBoxModel =
	                                    new DefaultComboBoxModel(flooringMaterialOptionComponents);

	public static final String PLASTER_BOARD = "plaster board [m2/m2]";

	public static final String[] innerWallOptionComponents = {

		PLASTER_BOARD,
		STEEL_FRAME,
		TILE,
		PAINT,
		PORTLAND_CEMENT,
		B_CLASS_CEMENT,
		SAND
	};

	public static final DefaultComboBoxModel innerWallComponentsComboBoxModel = new DefaultComboBoxModel(innerWallOptionComponents);

	public static final String[] innerWindowOptionComponents = {

		GLASS,
		METAL_FITTINGS,
		WOOD_FITTINGS
	};

	public static final DefaultComboBoxModel innerWindowComponentsComboBoxModel =
	                                    new DefaultComboBoxModel(innerWindowOptionComponents);

	public static final String ROCK_WOOL_SOUNDPROOFING = "rock wool soundproofing [m2/m2]";
	public static final String ALUMINUM_PANEL = "aluminum panel [m2/m2]";

	public static final String[] ceilingOptionComponents = {

		ROCK_WOOL_SOUNDPROOFING,
		ALUMINUM_PANEL,
		STEEL_FRAME,
		PAINT
	};

	public static final DefaultComboBoxModel ceilingComponentsComboBoxModel =
	                                    new DefaultComboBoxModel(ceilingOptionComponents);

	public static final String[] insulationOptionComponents = {

		FOAM_TYPE_INSULATION,
		GLASS_WOOL_INSULATION
	};

	public static final DefaultComboBoxModel insulationComponentsComboBoxModel =
	                                    new DefaultComboBoxModel(insulationOptionComponents);

    // material component combo box
	public static final String FLOOR_MATERIAL = "floor material";
	public static final String INNER_WINDOWS = "inner windows";

	// BuildingLCAInputSpecificationPanel Labels
	public static final JLabel buildingTypeLabel = Templates.makeLabel(BUILDING_TYPE);
	public static final JLabel constructionTypeLabel = Templates.makeLabel(CONSTRUCTION_TYPE);
	public static final JLabel floorAreaLabel = Templates.makeLabel(FLOOR_AREA);
	public static final JLabel evaluationPeriodLabel = Templates.makeLabel(EVALUATION_PERIOD);
	public static final JLabel baseLifetimeLabel = Templates.makeLabel(BASE_LIFETIME);
	public static final JLabel modifiedLifetimeLabel = Templates.makeLabel(MODIFIED_LIFETIME);

	public static final JLabel baseAirEquipmentLabel = Templates.makeLabel(BASE_AIR_EQUIPMENT);
	public static final JLabel modifiedAirEquipmentLabel = Templates.makeLabel(MODIFIED_AIR_EQUIPMENT);
	public static final JLabel baseLightingLabel = Templates.makeLabel(BASE_LIGHTING);
	public static final JLabel modifiedLightingLabel = Templates.makeLabel(MODIFIED_LIGHTING);
	public static final JLabel areaPerRoomType1Label = Templates.makeLabel(AREA_PER_ROOM_TYPE1);
	public static final JLabel areaPerRoomType2Label = Templates.makeLabel(AREA_PER_ROOM_TYPE2);

	public static final JLabel baseLabel = Templates.makeLabel(BASE);
	public static final JLabel modifiedLabel = Templates.makeLabel(MODIFIED);

	public static final JLabel yearUnitLabel1 = Templates.makeLabel(YEAR_UNIT);
	public static final JLabel yearUnitLabel2 = Templates.makeLabel(YEAR_UNIT);
	public static final JLabel yearUnitLabel3 = Templates.makeLabel(YEAR_UNIT);
	public static final JLabel yearUnitLabel4 = Templates.makeLabel(YEAR_UNIT);
	public static final JLabel KwhPerYearUnitLabel1 = Templates.makeLabel(KWH_PER_YEAR_UNIT);
	public static final JLabel KwhPerYearUnitLabel2 = Templates.makeLabel(KWH_PER_YEAR_UNIT);
	public static final JLabel KwhPerYearUnitLabel3 = Templates.makeLabel(KWH_PER_YEAR_UNIT);
	public static final JLabel KwhPerYearUnitLabel4 = Templates.makeLabel(KWH_PER_YEAR_UNIT);
	public static final JLabel m2UnitLabel1 = Templates.makeLabel(M2_UNIT);
	public static final JLabel m2UnitLabel4 = Templates.makeLabel(M2_UNIT);
	public static final JLabel m2UnitLabel5 = Templates.makeLabel(M2_UNIT);
	public static final JLabel m2kgUnitLabel1 = Templates.makeLabel(KG_M2_UNIT);
	public static final JLabel m2kgUnitLabel2 = Templates.makeLabel(KG_M2_UNIT);

	// BuildingLCAInputSpecificationPanel Borders
	public static final String BASE_CHARACTERISTICS_BORDER = "base characteristics";
	public static final String BUILDING_CONSTRUCTION_INFORMATION_BORDER = "building construction information";
	public static final String ENERGY_CONSUMPTION_CALCULATION_BORDER = "energy consumption calculation";

	// ResultsPanel borders
	public static final String BUILDING_LCA_RESULTS_BORDER = "building lca results";

	// input Parameter names
	public static final String BUILDING_TYPE_ID_PARAMETER = "building type id";
	public static final String CONSTRUCTION_TYPE_ID_PARAMETER = "construction type id";
	public static final String TOTAL_FLOOR_AREA_PARAMETER = "total floor area";
	public static final String EVALUATION_PERIOD_PARAMETER = "evaluation period";
	public static final String BASE_DESIGN_LIFETIME_PARAMETER = "base design lifetime";
	public static final String MODIFIED_DESIGN_LIFETIME_PARAMETER = "modified design lifetime";
	public static final String BASE_AIR_CIRCULATION_PARAMETER = "base air circ";
	public static final String MODIFIED_AIR_CIRCULATION_PARAMETER = "modified air circ";
	public static final String BASE_LIGHTING_PARAMETER = "base lighting";
	public static final String MODIFIED_LIGHTING_PARAMETER = "modified lighting";
	public static final String BASE_CONSTRUCTION_DATA_PARAMETER = "base construction data";
	public static final String MODIFIED_CONSTRUCTION_DATA_PARAMETER = "modified construction data";

	// output Parameter names
	public static final String LCE_PARAMETER = "LCE output";
	public static final String NOX_PARAMETER = "LCNOx output";
	public static final String SOX_PARAMETER = "LCSOx output";
	public static final String CO2_PARAMETER = "LCCO2 output";
	public static final String COST_PARAMETER = "LCCost output";

	// combo box for outputs
	public static final String LCE = "LCE";
	public static final String LC_CO2 = "LC CO2";
	public static final String LC_NOX = "LC NOx";
	public static final String LC_SOX = "LC SOx";
	public static final String LC_COST = "LC cost";

	public static final String[] outputChoices = {

		LCE,
		LC_CO2,
		LC_NOX,
		LC_SOX,
		LC_COST
	};

	// ResultsPanel chart constants
	public static final String HORIZONTAL_AXIS = "horizontal axis";
	public static final String VERTICAL_AXIS = "vertical axis";

	// LCE constants
	public static final String TOP = "top";
	public static final String BOTTOM = "bottom";

	public static final String[] resultLegends = {

		TOP,
		BOTTOM
	};

	public static final List componentsComboBoxList = Arrays.asList(new Object[]
	{
		engineeringOptionsComboBoxModel,
		structureOptionsComboBoxModel,
		outerShellComboBoxModel,
		innerShellComboBoxModel
	});

	public static final List engineeringComboBoxList = Arrays.asList(new Object[]
	{
        earthComponentsComboBoxModel,
        pilingsComponentsComboBoxModel
	});

	public static final List structureComboBoxList = Arrays.asList(new Object[]
	{
		structureComponentsComboBoxModel
	});

	public static final List outerShellComboBoxList = Arrays.asList(new Object[]
	{
		roofComponentsComboBoxModel,
		outerWallComponentsComboBoxModel,
		outerWindowComponentsComboBoxModel,

	});

	public static final List innerShellComboBoxList = Arrays.asList(new Object[]
	{
    	flooringMaterialComponentsComboBoxModel,
	    innerWallComponentsComboBoxModel,
	    innerWindowComponentsComboBoxModel,
	    ceilingComponentsComboBoxModel,
	    insulationComponentsComboBoxModel
	});

	public static final Vector comboBoxes = new Vector(Arrays.asList(new Object[]
	{
		engineeringComboBoxList,
		structureComboBoxList,
		outerShellComboBoxList,
		innerShellComboBoxList
	}));


}
