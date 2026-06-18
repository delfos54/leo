package com.example.coffee.connect.Controller;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.example.coffee.connect.dto.ProductosDTO;
import com.example.coffee.connect.model.Productos;
import com.example.coffee.connect.security.JwtUtil;
import com.example.coffee.connect.service.ProductosService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean; // ◄ Usamos el MockBean estándar unificado

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

@WebMvcTest(ProductosController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean // ◄ CORREGIDO: Unificado a @MockBean para evitar fallos de inicialización del contexto
    private ProductosService service;

    @MockBean // ◄ CORREGIDO: Mantenido en armonía con el mock anterior
    private JwtUtil jwtUtil;

    @Test
    void debeListarProductos() throws Exception {
        Productos p = new Productos();
        p.setId(1L);
        p.setNombre("Café Latte");
        p.setPrecio(new BigDecimal("2500"));
        p.setIsHot(true);

        when(service.listar()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(soundnessCheck("Listado obtenido"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].nombre").value("Café Latte"));
    }

    @Test
    void debeObtenerProductoPorId() throws Exception {
        Productos p = new Productos();
        p.setId(1L);
        p.setNombre("Espresso");
        p.setPrecio(new BigDecimal("1500"));
        p.setIsHot(true);

        when(service.obtener(1L)).thenReturn(p);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(soundnessCheck("Producto obtenido"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nombre").value("Espresso"))
                .andExpect(jsonPath("$.data._links.self.href").exists())
                .andExpect(jsonPath("$.data._links.all.href").exists());
    }

    @Test
    void debeCrearProducto() throws Exception {
        ProductosDTO dto = new ProductosDTO();
        dto.setNombre("Capuccino");
        dto.setDescripcion("Café con espuma");
        dto.setPrecio(new BigDecimal("2800"));
        dto.setHot(true);
        dto.setStock(10);

        Productos creado = new Productos();
        creado.setId(1L);
        creado.setNombre("Capuccino");

        when(service.crear(any(ProductosDTO.class))).thenReturn(creado);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(soundnessCheck("Producto creado"));
    }

    @Test
    void debeActualizarProducto() throws Exception {
        ProductosDTO dto = new ProductosDTO();
        dto.setNombre("Café Premium");
        dto.setPrecio(new BigDecimal("3500"));
        dto.setHot(false);

        Productos actualizado = new Productos();
        actualizado.setId(1L);
        actualizado.setNombre("Café Premium");

        when(service.actualizar(eq(1L), any(ProductosDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(soundnessCheck("Producto actualizado"));
    }

    @Test
    void debeEliminarProducto() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(soundnessCheck("Producto eliminado"));
    }

    @Test
    void debeObtenerPrecioDelProducto() throws Exception {
        Productos p = new Productos();
        p.setId(1L);
        p.setPrecio(new BigDecimal("3500"));

        when(service.obtener(1L)).thenReturn(p);

        mockMvc.perform(get("/api/productos/1/precio"))
                .andExpect(status().isOk())
                .andExpect(content().string("3500"));
    }

    private static org.springframework.test.web.servlet.ResultMatcher soundnessCheck(String expectedMessage) {
        return result -> {
            jsonPath("$.respuesta").value(true).match(result);
            jsonPath("$.mensaje").value(expectedMessage).match(result);
        };
    }
}