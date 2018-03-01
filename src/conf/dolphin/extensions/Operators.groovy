package pt.lsts.dolphin.dsl.imc
import pt.lsts.dolphin.runtime.Position
import pt.lsts.imc.dsl.Location

/**
 * Position & area operators
 * Conversion between the IMC DSL to generate plans Location class and Dolphin Position class
 * @return Location
 */
Position.metaClass.asType << { 
  Class clazz -> clazz == Location.class ? 
      new Location(delegate.lat * Position.R2D, delegate.lon * Position.R2D) : null; 
}

     
    x = Position.fromDegrees(35, -8, 0)
    y = x as Location
    
    print y

