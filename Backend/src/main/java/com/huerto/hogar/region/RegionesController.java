package com.huerto.hogar.region;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:5273")
@RestController
@RequestMapping("/api/regiones")
@Tag(name = "Regiones y Comunas")
public class RegionesController {

    private static final Map<String, List<String>> DATOS = new LinkedHashMap<>();
    static {
        DATOS.put("Arica y Parinacota",          List.of("Arica","Camarones","Putre","General Lagos"));
        DATOS.put("Tarapacá",                    List.of("Iquique","Alto Hospicio","Pozo Almonte","Camiña","Colchane","Huara","Pica"));
        DATOS.put("Antofagasta",                 List.of("Antofagasta","Mejillones","Sierra Gorda","Taltal","Calama","Ollagüe","San Pedro de Atacama","Tocopilla","María Elena"));
        DATOS.put("Atacama",                     List.of("Copiapó","Caldera","Tierra Amarilla","Chañaral","Diego de Almagro","Vallenar","Alto del Carmen","Freirina","Huasco"));
        DATOS.put("Coquimbo",                    List.of("La Serena","Coquimbo","Andacollo","La Higuera","Paihuano","Vicuña","Illapel","Los Vilos","Salamanca","Ovalle","Monte Patria"));
        DATOS.put("Valparaíso",                  List.of("Valparaíso","Viña del Mar","Quilpué","Villa Alemana","Concón","Casablanca","Quillota","San Antonio","San Felipe","Los Andes"));
        DATOS.put("Metropolitana de Santiago",   List.of("Santiago","Providencia","Las Condes","Maipú","La Florida","Puente Alto","San Bernardo","Ñuñoa","Vitacura","Lo Barnechea","Peñalolén","Estación Central"));
        DATOS.put("O'Higgins",                   List.of("Rancagua","San Fernando","Pichilemu","Machalí","Rengo","Graneros","Requínoa"));
        DATOS.put("Maule",                       List.of("Talca","Curicó","Linares","Constitución","Cauquenes","Molina","Parral","San Javier"));
        DATOS.put("Ñuble",                       List.of("Chillán","Chillán Viejo","Bulnes","San Carlos","Coihueco","Quirihue","Yungay"));
        DATOS.put("Biobío",                      List.of("Concepción","Talcahuano","Los Ángeles","Chiguayante","Coronel","San Pedro de la Paz","Hualpén","Tomé","Lota","Penco","Lebu","Los Álamos"));
        DATOS.put("La Araucanía",                List.of("Temuco","Padre Las Casas","Villarrica","Pucón","Angol","Lautaro","Freire","Nueva Imperial","Curacautín"));
        DATOS.put("Los Ríos",                    List.of("Valdivia","La Unión","Río Bueno","Futrono","Lago Ranco","Panguipulli","Lanco"));
        DATOS.put("Los Lagos",                   List.of("Puerto Montt","Puerto Varas","Osorno","Castro","Ancud","Frutillar","Calbuco","Llanquihue"));
        DATOS.put("Aysén",                       List.of("Coyhaique","Aysén","Chile Chico","Cochrane","Río Ibáñez"));
        DATOS.put("Magallanes",                  List.of("Punta Arenas","Natales","Torres del Paine","Porvenir"));
    }

    @GetMapping
    public ResponseEntity<List<String>> listarRegiones() {
        return ResponseEntity.ok(new ArrayList<>(DATOS.keySet()));
    }

    @GetMapping("/{region}/comunas")
    public ResponseEntity<List<String>> comunas(@PathVariable String region) {
        List<String> comunas = DATOS.entrySet().stream()
            .filter(e -> e.getKey().equalsIgnoreCase(region))
            .map(Map.Entry::getValue).findFirst().orElse(List.of());
        return ResponseEntity.ok(comunas);
    }

    @GetMapping("/todas")
    public ResponseEntity<Map<String, List<String>>> todas() {
        return ResponseEntity.ok(DATOS);
    }
}
