package com.lspb.mapamanual;

import com.formdev.flatlaf.FlatLightLaf;
//import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
//import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTAtomOneDarkIJTheme;
//import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTAtomOneLightIJTheme;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

/** @author Compiladores **/

public class FramePrincipal extends javax.swing.JFrame {
    
    public static TileFactoryInfo informacion;
    public static JXMapViewer Mapa = new JXMapViewer();
    public static GeoPosition Spawn = new GeoPosition(24.024043, -104.670272);
    public static List<Waypoint> waypoints = new ArrayList<>();
    public static WaypointPainter<Waypoint> painter = new WaypointPainter<>();
    public static boolean inicio = true;
    public static boolean necesarioRecargar = true;
    public static boolean variosWaypoints = false;
    public static int Tema=0;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FramePrincipal.class.getName());

    //CREACION DE "FramePrincipal" INICIA LOS COMPONENTES NECESARIOS
    public FramePrincipal() {
        FlatLightLaf.setup();
        initComponents();
        setLocationRelativeTo(null);
        informacion = new OSMTileFactoryInfo("OpenStreetMap", "https://tile.openstreetmap.org");
        ConfigurarMapa();
    }

    //METODO "ConfigurarMapa" SE ENCARGA DE INICIALIZAR EL MAPA, CARGAR EL PUNTO DE PARTIDA Y MOSTRARLA EN EL PANEL: "PanelMapa"
    private void ConfigurarMapa(){
        
        DefaultTileFactory tileFactory = new DefaultTileFactory(informacion);
        Mapa.setTileFactory(tileFactory);
        
        if(inicio){
            inicio = false;
            //PUNTO DE SPAWN (PLAZA DE ARMAS, DGO.)
            Mapa.setCenterPosition(Spawn);
            Mapa.setZoom(1);
            
            // METODO PARA ACTUALIZAR LA ETIQUETA DE COORDENADAS EN TIEMPO REAL-----//|
            Runnable actualizarCoord = () -> {                                      //|
                GeoPosition centro = Mapa.getCenterPosition();                      //|
                if (centro != null) {                                               //|
                    txtCoordenadas.setText(String.format("Coordenadas: %.6f, %.6f", //|
                        centro.getLatitude(), centro.getLongitude()));              //|
                }                                                                   //|
            };                                                                      //|
                                                                                    //|
            // ARRANQUE INICIAL DEL METODO------------------------------------------//|
            actualizarCoord.run();                                                  //|
                                                                                    //|
            // LISTENERS QUE INVOCAN AL METODO CUANDO EL MOUSE INTERACTUA-----------//|
            Mapa.addMouseListener(new java.awt.event.MouseAdapter() {               //|
                @Override                                                           //|
                public void mouseReleased(java.awt.event.MouseEvent e) {            //|
                    actualizarCoord.run();                                          //|
                }                                                                   //|
            });                                                                     //|
            Mapa.addMouseMotionListener(new java.awt.event.MouseAdapter() {         //|
                @Override                                                           //|
                public void mouseDragged(java.awt.event.MouseEvent e) {             //|
                    actualizarCoord.run();                                          //|
                }                                                                   //|
            });                                                                     //|
                                                                                    //|
            //LISTENERS QUE INVOCAN AL METODO CON LOS CAMBIOS DE ZOOM---------------//|
            Mapa.addPropertyChangeListener("center", evt -> actualizarCoord.run()); //|
            Mapa.addPropertyChangeListener("zoom", evt -> actualizarCoord.run());   //|
            //FIN-------------------------------------------------------------------//|
                                                                                    
            //CREAR EVENTOS QUE SE ACCIONEN CUANDO EL MOUSE INTERACTUA CON EL MAPA
            MouseInputListener AccionMouse = new PanMouseInputListener(Mapa);
            Mapa.addMouseListener(AccionMouse);
            Mapa.addMouseMotionListener(AccionMouse);
            Mapa.addMouseWheelListener(new ZoomMouseWheelListenerCursor(Mapa));
            
            Mapa.setLayout(new BorderLayout());
            
            //INTRODUCIR LA BARRA DE BUSQUEDAS A LA BARRA SUPERIOR
            Restaurar.setPreferredSize(new java.awt.Dimension(27, 27));
            CuadroBusqueda.setColumns(18);
            JPanel ContenedorBarraBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
            ContenedorBarraBusqueda.setOpaque(false);
            ContenedorBarraBusqueda.add(Box.createHorizontalStrut(10));
            ContenedorBarraBusqueda.add(Restaurar);
            ContenedorBarraBusqueda.add(Box.createHorizontalStrut(5));
            ContenedorBarraBusqueda.add(CuadroBusqueda);
            ContenedorBarraBusqueda.add(Buscar);
            CuadroBusqueda.setText("Ingrese un lugar para buscar...");
            CuadroBusqueda.setForeground(Color.GRAY);
            
            //INTRODUCIR EL LABEL DE COORDENADAS A LA BARRA SUPERIOR
            txtCoordenadas.setForeground(Color.WHITE);
            JPanel ContenedorCoordenadas = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
            ContenedorCoordenadas.setOpaque(false);
            ContenedorCoordenadas.add(txtCoordenadas);
            ContenedorCoordenadas.add(Box.createHorizontalStrut(90));
            
            //INTRODUCIR EL COMBOBOX DE OPCIONES VISUALES A LA BARRA SUPERIOR
            BtnConfiguracion.setPreferredSize(new Dimension(27, 27));
            JPanel ContenedorCuadroOpciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
            ContenedorCuadroOpciones.setOpaque(false);
            ContenedorCuadroOpciones.add(CuadroOpciones);
            ContenedorCuadroOpciones.add(Box.createHorizontalStrut(5));
            ContenedorCuadroOpciones.add(BtnConfiguracion);
            ContenedorCuadroOpciones.add(Box.createHorizontalStrut(10));
            
            //INTRODUCIR LA BARRA SUPERIOR AL MAPA
            JPanel Superior = new JPanel (new BorderLayout()){
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    g2.setColor(Color.DARK_GRAY);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            
            Superior.setOpaque(false);
            Superior.add(ContenedorBarraBusqueda, BorderLayout.WEST);
            Superior.add(ContenedorCoordenadas, BorderLayout.CENTER);
            Superior.add(ContenedorCuadroOpciones, BorderLayout.EAST);
            Mapa.add(Superior, BorderLayout.NORTH);
            
            //INTRODUCIR LOS BOTONES DE ZOOM A LA BARRA LATERAL
            BtnAcercar.setPreferredSize(new Dimension(45, 45));
            BtnAcercar.setMinimumSize(new Dimension(45, 45));
            BtnAcercar.setMaximumSize(new Dimension(45, 45));
            BtnAlejar.setPreferredSize(new Dimension(45, 45));
            BtnAlejar.setMinimumSize(new Dimension(45, 45));
            BtnAlejar.setMaximumSize(new Dimension(45, 45));
            BtnWaypoint.setPreferredSize(new Dimension(45, 45));
            BtnWaypoint.setMinimumSize(new Dimension(45, 45));
            BtnWaypoint.setMaximumSize(new Dimension(45, 45));
            JPanel ContenedorBotonesZoom = new JPanel();
            ContenedorBotonesZoom.setLayout(new BoxLayout(ContenedorBotonesZoom, BoxLayout.Y_AXIS));
            ContenedorBotonesZoom.setOpaque(false);
            ContenedorBotonesZoom.add(BtnAcercar);
            ContenedorBotonesZoom.add(Box.createVerticalStrut(1));
            ContenedorBotonesZoom.add(BtnAlejar);
            ContenedorBotonesZoom.add(Box.createVerticalStrut(10));
            ContenedorBotonesZoom.add(BtnWaypoint);
            ContenedorBotonesZoom.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
            
            //INTRODUCIR LA BARRA LATERAL AL MAPA
            JPanel BarraLateral = new JPanel(new BorderLayout());
            
            BarraLateral.setOpaque(false);
            BarraLateral.add(ContenedorBotonesZoom, BorderLayout.EAST);
            Mapa.add(BarraLateral, BorderLayout.SOUTH);
            
            //CONFIGURAR MARCADORES (WAYPOINTS)
            waypoints.add(new DefaultWaypoint(Spawn));
            updateWaypoints();
            
            Mapa.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    // Convierte las coordenadas X,Y del clic en coordenadas geográficas
                    GeoPosition clickedPosition = Mapa.convertPointToGeoPosition(evt.getPoint());

                    // Llama a tu método para agregar el waypoint y repintar el mapa
                    agregarWaypointAlHacerClic(clickedPosition);
                }
            });
            
            //INTRODUCIR EL OBJETO "Mapa" EN EL PANEL "PanelMapa"
            PanelMapa.setLayout(new BorderLayout());
            PanelMapa.add(Mapa, BorderLayout.CENTER);
        }
    }
    
    private void agregarWaypointAlHacerClic(GeoPosition posicion) {
        DefaultWaypoint nuevoWaypoint = new DefaultWaypoint(posicion);

        if(!variosWaypoints){
            waypoints.clear();
        }
        waypoints.add(nuevoWaypoint);
        
        updateWaypoints();

        System.out.println("Waypoint agregado en: " + posicion.getLatitude() + ", " + posicion.getLongitude());
    }
    
    private void updateWaypoints() {
        painter.setWaypoints(new LinkedHashSet<>(waypoints));
        Mapa.setOverlayPainter(painter);
        if(necesarioRecargar){
            Mapa.setCenterPosition(waypoints.get(waypoints.size() - 1).getPosition());
            Mapa.setZoom(1);
        }
    }
    
    private GeoPosition GeoCodificarDireccion(String Direccion) {
        try {
            String urlString = "https://nominatim.openstreetmap.org/search?q="
                    + java.net.URLEncoder.encode(Direccion, "UTF-8")
                    + "&format=json&limit=1";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .header("User-Agent", "MapaVirtual_1.0") // Obligatorio
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Error HTTP: " + response.statusCode());
                return null;
            }

            JSONArray resultados = new JSONArray(response.body());
            if (resultados.isEmpty()) {
                return null;
            }

            JSONObject primero = resultados.getJSONObject(0);
            double lat = primero.getDouble("lat");
            double lon = primero.getDouble("lon");
            return new GeoPosition(lat, lon);

        } catch (IOException | InterruptedException | JSONException ex) {
            System.out.println("Error al consultar datos: "+ex);
            return null;
        }
    }
    
    /**
     * (ENG)
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     * 
     * (ESP)
     * Este método se llama desde el constructor para inicializar el formulario.
     * ADVERTENCIA: NO modifique este código. El contenido de este método siempre
     * es regenerado por el Editor de Formularios.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelMapa = new javax.swing.JPanel();
        CuadroOpciones = new javax.swing.JComboBox<>();
        CuadroBusqueda = new javax.swing.JTextField();
        Buscar = new javax.swing.JButton();
        Restaurar = new javax.swing.JButton();
        txtCoordenadas = new javax.swing.JLabel();
        BtnAcercar = new javax.swing.JButton();
        BtnAlejar = new javax.swing.JButton();
        BtnConfiguracion = new javax.swing.JButton();
        BtnWaypoint = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Mapa Virtual");

        CuadroOpciones.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mapa de Open Street", "Mapa de Virtual Earth", "Hibrido", "Satelite" }));
        CuadroOpciones.addActionListener(this::CuadroOpcionesActionPerformed);

        CuadroBusqueda.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        CuadroBusqueda.setText("Ingrese un lugar para buscar...");
        CuadroBusqueda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                CuadroBusquedaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                CuadroBusquedaFocusLost(evt);
            }
        });

        Buscar.setText("Ir");
        Buscar.addActionListener(this::BuscarActionPerformed);

        Restaurar.setText("🔄");
        Restaurar.addActionListener(this::RestaurarActionPerformed);

        txtCoordenadas.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtCoordenadas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtCoordenadas.setText("Coordenadas: 24.024043, -104.670272");

        BtnAcercar.setText("+");
        BtnAcercar.addActionListener(this::BtnAcercarActionPerformed);

        BtnAlejar.setText("-");
        BtnAlejar.addActionListener(this::BtnAlejarActionPerformed);

        BtnConfiguracion.setText("🔧");
        BtnConfiguracion.addActionListener(this::BtnConfiguracionActionPerformed);

        BtnWaypoint.setText("📍");
        BtnWaypoint.addActionListener(this::BtnWaypointActionPerformed);

        javax.swing.GroupLayout PanelMapaLayout = new javax.swing.GroupLayout(PanelMapa);
        PanelMapa.setLayout(PanelMapaLayout);
        PanelMapaLayout.setHorizontalGroup(
            PanelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(PanelMapaLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(Restaurar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CuadroBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78)
                .addComponent(txtCoordenadas)
                .addGap(117, 117, 117)
                .addGroup(PanelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PanelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(BtnWaypoint, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(PanelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(BtnAlejar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BtnAcercar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PanelMapaLayout.createSequentialGroup()
                        .addComponent(CuadroOpciones, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BtnConfiguracion)))
                .addGap(16, 16, 16))
        );
        PanelMapaLayout.setVerticalGroup(
            PanelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelMapaLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(PanelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CuadroOpciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnConfiguracion)
                    .addComponent(CuadroBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Buscar)
                    .addComponent(Restaurar)
                    .addComponent(txtCoordenadas))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 418, Short.MAX_VALUE)
                .addComponent(BtnAcercar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BtnAlejar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BtnWaypoint, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelMapa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelMapa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CuadroOpcionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CuadroOpcionesActionPerformed
        switch(String.valueOf(CuadroOpciones.getSelectedItem())){
            case "Mapa de Open Street" -> informacion = new OSMTileFactoryInfo("", "https://tile.openstreetmap.org");
                
            case "Mapa de Virtual Earth" -> informacion = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP);
                
            case "Hibrido" -> informacion = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.HYBRID);
                
            case "Satelite" -> informacion = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.SATELLITE);
        }
        ConfigurarMapa();
    }//GEN-LAST:event_CuadroOpcionesActionPerformed

    private void CuadroBusquedaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CuadroBusquedaFocusGained
        if(CuadroBusqueda.getText().equals("Ingrese un lugar para buscar...")){
            CuadroBusqueda.setText("");
            CuadroBusqueda.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_CuadroBusquedaFocusGained

    private void CuadroBusquedaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CuadroBusquedaFocusLost
        if(CuadroBusqueda.getText().equals("")){
            CuadroBusqueda.setText("Ingrese un lugar para buscar...");
            CuadroBusqueda.setForeground(Color.GRAY);
        }
    }//GEN-LAST:event_CuadroBusquedaFocusLost

    private void RestaurarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RestaurarActionPerformed
        waypoints.clear();
        waypoints.add(new DefaultWaypoint(Spawn));
        updateWaypoints();
    }//GEN-LAST:event_RestaurarActionPerformed

    private void BtnConfiguracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnConfiguracionActionPerformed
        ConfiguracionMapa setup = new ConfiguracionMapa();
        setup.setVisible(true);
    }//GEN-LAST:event_BtnConfiguracionActionPerformed

    private void BtnAcercarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAcercarActionPerformed
        int zoom = Mapa.getZoom();
        Mapa.setZoom(zoom-1);
    }//GEN-LAST:event_BtnAcercarActionPerformed

    private void BtnAlejarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAlejarActionPerformed
        int zoom = Mapa.getZoom();
        Mapa.setZoom(zoom+1);
    }//GEN-LAST:event_BtnAlejarActionPerformed

    private void BtnWaypointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnWaypointActionPerformed
        Mapa.setCenterPosition(waypoints.get(waypoints.size() - 1).getPosition());
        Mapa.setZoom(1);
    }//GEN-LAST:event_BtnWaypointActionPerformed

    private void BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BuscarActionPerformed
        String Direccion = CuadroBusqueda.getText().trim();
        if (Direccion.isEmpty() || Direccion.equals("Ingrese un lugar para buscar...")) {
            JOptionPane.showMessageDialog(this, "Ingresa un lugar para buscar");
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        GeoPosition posicion = GeoCodificarDireccion(Direccion);
        setCursor(Cursor.getDefaultCursor());

        if (posicion != null) {
            agregarWaypointAlHacerClic(posicion);
            Mapa.setAddressLocation(posicion);
        } else {
            JOptionPane.showMessageDialog(this, "No se encontro la ubicación:\n" + Direccion,
                    "Busqueda fallida", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_BuscarActionPerformed

    /** @param args the command line arguments **/
    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FramePrincipal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnAcercar;
    private javax.swing.JButton BtnAlejar;
    private javax.swing.JButton BtnConfiguracion;
    private javax.swing.JButton BtnWaypoint;
    private javax.swing.JButton Buscar;
    private javax.swing.JTextField CuadroBusqueda;
    private javax.swing.JComboBox<String> CuadroOpciones;
    private javax.swing.JPanel PanelMapa;
    private javax.swing.JButton Restaurar;
    private javax.swing.JLabel txtCoordenadas;
    // End of variables declaration//GEN-END:variables
}
