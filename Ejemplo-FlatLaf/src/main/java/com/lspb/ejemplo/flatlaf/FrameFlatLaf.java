/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.lspb.ejemplo.flatlaf;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.Animator.TimingTarget;
import com.formdev.flatlaf.util.ColorFunctions;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author santiago
 */
public class FrameFlatLaf extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrameFlatLaf.class.getName());

    /**
     * Creates new form FrameFlatLaf
     */
    // Componentes del menú desplegable
    private Animator animador; 
    private int anchoMenu = 220;
    private boolean menuVisible = false;
    
    private Color baseColorMenu; 
    private Color baseColorOscuro;
    private Color baseColorClaro;
    
    public FrameFlatLaf() {
        initComponents();
        
        
        
        FlatMacLightLaf.setup();
        
        getContentPane().removeAll();
        // Establecer BorderLayout
        getContentPane().setLayout(new BorderLayout());

        // Agregar el menú a la izquierda (WEST)
        getContentPane().add(PanelMenu, BorderLayout.WEST);
        // Agregar el panel principal al centro (CENTER) - este es el que contiene todos tus objetos
        getContentPane().add(ContenedorPrincipal, BorderLayout.CENTER);

        // Configurar PanelMenu con BoxLayout vertical (como ya tenías)
        PanelMenu.setLayout(new BoxLayout(PanelMenu, BoxLayout.Y_AXIS));
        PanelMenu.setPreferredSize(null);
        PanelMenu.setMaximumSize(null);

        // Ajustar los paneles internos del menú
        for (JPanel panel : new JPanel[]{ContenedorMenu, ContenedorTemaOscuro, ContenedorTemaClaro}) {
            panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            panel.setMinimumSize(new Dimension(0, 70));
            panel.setPreferredSize(new Dimension(220, 70));
            PanelMenu.add(panel);
        }

        // Estado inicial: menú contraído (solo íconos)
        anchoMenu = 65;
        menuVisible = false;
        txtMenu.setVisible(false);
        txtTemaClaro.setVisible(false);
        txtTemaOscuro.setVisible(false);
        for (JPanel panel : new JPanel[]{ContenedorMenu, ContenedorTemaOscuro, ContenedorTemaClaro}) {
            panel.setPreferredSize(new Dimension(anchoMenu, 70));
        }
        PanelMenu.setPreferredSize(new Dimension(anchoMenu, PanelMenu.getHeight()));

        // Forzar actualización del layout
        getContentPane().revalidate();

        cambiarTema(true);
        setLocationRelativeTo(null);

        // Colores base para efectos hover
        baseColorMenu = UIManager.getColor("Panel.background");
        baseColorOscuro = baseColorMenu;
        baseColorClaro = baseColorMenu;
    }
    
    private void toggleMenu() {
        // Si hay una animación en curso, la detenemos
        if (animador != null && animador.isRunning()) {
            animador.stop();
        }
        
        // Ancho final: 200 si se abre, 0 si se cierra
        if (menuVisible) {
            anchoMenu = 65;
        } else {
            anchoMenu = 220;
        }
        
        boolean mostrarTexto = (anchoMenu > 100);
        txtMenu.setVisible(mostrarTexto);
        txtTemaClaro.setVisible(mostrarTexto);
        txtTemaOscuro.setVisible(mostrarTexto);

        // Ajustar el tamaño preferido de los paneles internos para que tengan el ancho correcto
        for (JPanel panel : new JPanel[]{ContenedorMenu, ContenedorTemaOscuro, ContenedorTemaClaro}) {
            panel.setPreferredSize(new Dimension(anchoMenu, 70));
        }
        
        menuVisible = !menuVisible;
        
        // Guardamos el ancho inicial
        int anchoInicial = PanelMenu.getWidth();
        int delta = anchoMenu - anchoInicial;  // diferencia a recorrer
        
        // Creamos el TimingTarget para recibir eventos de animación
        TimingTarget target = new TimingTarget() {
            @Override
            public void timingEvent(float fraccion) {
                int nuevoAncho = Math.round(anchoInicial + delta * fraccion);
                SwingUtilities.invokeLater(() -> {
                    // 1. Cambiar el ancho del menú
                    PanelMenu.setPreferredSize(new Dimension(nuevoAncho, PanelMenu.getHeight()));
                    PanelMenu.revalidate();
                    PanelMenu.repaint();

                    getContentPane().revalidate();
                    getContentPane().repaint();
                });
            }
            
            @Override
            public void begin() {
                // Opcional: antes de comenzar
            }
            
            @Override
            public void end() {
                // Aseguramos el ancho final exacto
                SwingUtilities.invokeLater(() -> {
                    PanelMenu.setPreferredSize(new Dimension(anchoMenu, PanelMenu.getHeight()));
                    PanelMenu.revalidate();
                    PanelMenu.repaint();
                    getContentPane().revalidate();
                    getContentPane().repaint();
                });
            }
        };
        animador = new Animator(160, target);   // duración 300 ms
        animador.setResolution(1);             // opcional, 120 fps
        animador.start();
    }

    /**
     * Cambia el tema visual (claro/oscuro) con animación suave.
     * @param claro true = tema claro, false = tema oscuro
     */
    private void cambiarTema(boolean claro) {
        FlatAnimatedLafChange.showSnapshot();
        try {
            if (claro) {
                UIManager.setLookAndFeel(new FlatMacLightLaf());
            } else {
                UIManager.setLookAndFeel(new FlatMacDarkLaf());
            }
            FlatLaf.updateUI();
            // Restablecer los colores de fondo de los paneles internos al color por defecto del nuevo tema
            Color panelBg = UIManager.getColor("Panel.background");
            ContenedorMenu.setBackground(panelBg);
            ContenedorTemaOscuro.setBackground(panelBg);
            ContenedorTemaClaro.setBackground(panelBg);
            
            baseColorMenu = UIManager.getColor("Panel.background");
            baseColorOscuro = baseColorMenu;  // o si quieres colores distintos, ajusta
            baseColorClaro = baseColorMenu;
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, "Error cambiando tema", ex);
        } finally {
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        }
        // Opcional: actualizar el color de fondo del menú para que combine con el nuevo tema
        PanelMenu.setBackground(UIManager.getColor("Panel.background"));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        PanelMenu = new javax.swing.JPanel();
        ContenedorMenu = new javax.swing.JPanel();
        txtMenu = new javax.swing.JLabel();
        iconoMenu = new javax.swing.JLabel();
        ContenedorTemaOscuro = new javax.swing.JPanel();
        txtTemaOscuro = new javax.swing.JLabel();
        iconoLuna = new javax.swing.JLabel();
        ContenedorTemaClaro = new javax.swing.JPanel();
        txtTemaClaro = new javax.swing.JLabel();
        iconoSol = new javax.swing.JLabel();
        ContenedorPrincipal = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        PanelInferior = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        PanelIzquierdo = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        PanelCentral = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jSpinner1 = new javax.swing.JSpinner();
        jTextField1 = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        PanelMenu.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PanelMenu.setMinimumSize(new java.awt.Dimension(220, 210));
        PanelMenu.setPreferredSize(new java.awt.Dimension(220, 210));
        PanelMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PanelMenuMouseClicked(evt);
            }
        });
        PanelMenu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ContenedorMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ContenedorMenuMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ContenedorMenuMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ContenedorMenuMouseExited(evt);
            }
        });

        txtMenu.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        txtMenu.setText("Menú");
        txtMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtMenuMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtMenuMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txtMenuMouseExited(evt);
            }
        });

        iconoMenu.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        iconoMenu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconoMenu.setText("☰");
        iconoMenu.setMinimumSize(new java.awt.Dimension(64, 64));
        iconoMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                iconoMenuMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                iconoMenuMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                iconoMenuMouseExited(evt);
            }
        });

        javax.swing.GroupLayout ContenedorMenuLayout = new javax.swing.GroupLayout(ContenedorMenu);
        ContenedorMenu.setLayout(ContenedorMenuLayout);
        ContenedorMenuLayout.setHorizontalGroup(
            ContenedorMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ContenedorMenuLayout.createSequentialGroup()
                .addComponent(iconoMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        ContenedorMenuLayout.setVerticalGroup(
            ContenedorMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContenedorMenuLayout.createSequentialGroup()
                .addComponent(iconoMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(txtMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        PanelMenu.add(ContenedorMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 220, 70));

        ContenedorTemaOscuro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ContenedorTemaOscuroMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ContenedorTemaOscuroMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ContenedorTemaOscuroMouseExited(evt);
            }
        });

        txtTemaOscuro.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        txtTemaOscuro.setText("Tema Oscuro");
        txtTemaOscuro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtTemaOscuroMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtTemaOscuroMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txtTemaOscuroMouseExited(evt);
            }
        });

        iconoLuna.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        iconoLuna.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconoLuna.setText("☾");
        iconoLuna.setPreferredSize(new java.awt.Dimension(64, 64));
        iconoLuna.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                iconoLunaMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                iconoLunaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                iconoLunaMouseExited(evt);
            }
        });

        javax.swing.GroupLayout ContenedorTemaOscuroLayout = new javax.swing.GroupLayout(ContenedorTemaOscuro);
        ContenedorTemaOscuro.setLayout(ContenedorTemaOscuroLayout);
        ContenedorTemaOscuroLayout.setHorizontalGroup(
            ContenedorTemaOscuroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ContenedorTemaOscuroLayout.createSequentialGroup()
                .addComponent(iconoLuna, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTemaOscuro, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        ContenedorTemaOscuroLayout.setVerticalGroup(
            ContenedorTemaOscuroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContenedorTemaOscuroLayout.createSequentialGroup()
                .addComponent(iconoLuna, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(txtTemaOscuro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        PanelMenu.add(ContenedorTemaOscuro, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 220, 70));

        ContenedorTemaClaro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ContenedorTemaClaroMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ContenedorTemaClaroMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ContenedorTemaClaroMouseExited(evt);
            }
        });

        txtTemaClaro.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        txtTemaClaro.setText("Tema Claro");
        txtTemaClaro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtTemaClaroMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                txtTemaClaroMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                txtTemaClaroMouseExited(evt);
            }
        });

        iconoSol.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        iconoSol.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconoSol.setText("☼");
        iconoSol.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                iconoSolMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                iconoSolMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                iconoSolMouseExited(evt);
            }
        });

        javax.swing.GroupLayout ContenedorTemaClaroLayout = new javax.swing.GroupLayout(ContenedorTemaClaro);
        ContenedorTemaClaro.setLayout(ContenedorTemaClaroLayout);
        ContenedorTemaClaroLayout.setHorizontalGroup(
            ContenedorTemaClaroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ContenedorTemaClaroLayout.createSequentialGroup()
                .addComponent(iconoSol, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTemaClaro, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        ContenedorTemaClaroLayout.setVerticalGroup(
            ContenedorTemaClaroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContenedorTemaClaroLayout.createSequentialGroup()
                .addComponent(iconoSol, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(txtTemaClaro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        PanelMenu.add(ContenedorTemaClaro, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 220, 70));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("¡Hola!, bienvenido.");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);

        jCheckBox1.setText("jCheckBox1");

        jCheckBox2.setText("jCheckBox2");

        jRadioButton1.setText("jRadioButton1");

        jRadioButton2.setText("jRadioButton2");

        jRadioButton3.setText("jRadioButton3");

        jRadioButton4.setText("jRadioButton4");

        jLabel2.setFont(new java.awt.Font("Arial", 1, 26)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Titulo de prueba");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Texto de prueba");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout PanelInferiorLayout = new javax.swing.GroupLayout(PanelInferior);
        PanelInferior.setLayout(PanelInferiorLayout);
        PanelInferiorLayout.setHorizontalGroup(
            PanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelInferiorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(PanelInferiorLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox2, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                            .addComponent(jCheckBox1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                            .addComponent(jRadioButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                            .addComponent(jRadioButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(26, 26, 26)
                        .addGroup(PanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox2, 0, 127, Short.MAX_VALUE)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        PanelInferiorLayout.setVerticalGroup(
            PanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelInferiorLayout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelInferiorLayout.createSequentialGroup()
                        .addGroup(PanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelInferiorLayout.createSequentialGroup()
                                .addGroup(PanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(PanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jCheckBox2)
                                        .addComponent(jRadioButton2)
                                        .addComponent(jRadioButton3)))
                                .addGap(6, 6, 6))
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(PanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addGroup(PanelInferiorLayout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addGroup(PanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jRadioButton1)
                                    .addComponent(jRadioButton4)))
                            .addGroup(PanelInferiorLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(PanelInferiorLayout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(PanelInferiorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setText("Boton");

        jButton2.setText("Boton");

        jLabel4.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Aa");

        jLabel5.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Bb");

        jLabel6.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Cc");

        jLabel7.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Dd");

        jLabel8.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Ee");

        jLabel9.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Ff");

        javax.swing.GroupLayout PanelIzquierdoLayout = new javax.swing.GroupLayout(PanelIzquierdo);
        PanelIzquierdo.setLayout(PanelIzquierdoLayout);
        PanelIzquierdoLayout.setHorizontalGroup(
            PanelIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelIzquierdoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        PanelIzquierdoLayout.setVerticalGroup(
            PanelIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelIzquierdoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addGap(63, 63, 63)
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(76, 76, 76)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList1);

        jTextField1.setText("jTextField1");

        jPasswordField1.setText("jPasswordField1");

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane4.setViewportView(jTextArea2);

        javax.swing.GroupLayout PanelCentralLayout = new javax.swing.GroupLayout(PanelCentral);
        PanelCentral.setLayout(PanelCentralLayout);
        PanelCentralLayout.setHorizontalGroup(
            PanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCentralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        PanelCentralLayout.setVerticalGroup(
            PanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCentralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelCentralLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPasswordField1, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                        .addGap(7, 7, 7)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout ContenedorPrincipalLayout = new javax.swing.GroupLayout(ContenedorPrincipal);
        ContenedorPrincipal.setLayout(ContenedorPrincipalLayout);
        ContenedorPrincipalLayout.setHorizontalGroup(
            ContenedorPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContenedorPrincipalLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(ContenedorPrincipalLayout.createSequentialGroup()
                .addComponent(PanelIzquierdo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ContenedorPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelCentral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelInferior, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        ContenedorPrincipalLayout.setVerticalGroup(
            ContenedorPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ContenedorPrincipalLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(ContenedorPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ContenedorPrincipalLayout.createSequentialGroup()
                        .addComponent(PanelCentral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelInferior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(PanelIzquierdo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(7, 7, 7))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(PanelMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ContenedorPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ContenedorPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(PanelMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void PanelMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelMenuMouseClicked

    }//GEN-LAST:event_PanelMenuMouseClicked

    private void txtMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtMenuMouseClicked
        toggleMenu();
    }//GEN-LAST:event_txtMenuMouseClicked

    private void iconoMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconoMenuMouseClicked
        toggleMenu();
    }//GEN-LAST:event_iconoMenuMouseClicked

    private void ContenedorMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContenedorMenuMouseClicked
        toggleMenu();
    }//GEN-LAST:event_ContenedorMenuMouseClicked

    private void ContenedorMenuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContenedorMenuMouseEntered
        Color colorOscuroFlat = ColorFunctions.darken(baseColorMenu, 0.15f);
        ContenedorMenu.setBackground(colorOscuroFlat);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ContenedorMenuMouseEntered

    private void txtMenuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtMenuMouseEntered
        Color colorOscuroFlat = ColorFunctions.darken(baseColorMenu, 0.15f);
        ContenedorMenu.setBackground(colorOscuroFlat);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_txtMenuMouseEntered

    private void iconoMenuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconoMenuMouseEntered
        Color colorOscuroFlat = ColorFunctions.darken(baseColorMenu, 0.15f);
        ContenedorMenu.setBackground(colorOscuroFlat);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_iconoMenuMouseEntered

    private void ContenedorTemaOscuroMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContenedorTemaOscuroMouseEntered
        Color colorOscuroFlat = ColorFunctions.darken(baseColorOscuro, 0.15f);
        ContenedorTemaOscuro.setBackground(colorOscuroFlat);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ContenedorTemaOscuroMouseEntered
        
    private void txtTemaOscuroMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTemaOscuroMouseEntered
        Color colorOscuroFlat = ColorFunctions.darken(baseColorOscuro, 0.15f);
        ContenedorTemaOscuro.setBackground(colorOscuroFlat);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_txtTemaOscuroMouseEntered

    private void iconoLunaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconoLunaMouseEntered
        Color colorOscuroFlat = ColorFunctions.darken(baseColorOscuro, 0.15f);
        ContenedorTemaOscuro.setBackground(colorOscuroFlat);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_iconoLunaMouseEntered

    private void ContenedorTemaClaroMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContenedorTemaClaroMouseEntered
        Color colorOscuroFlat = ColorFunctions.darken(baseColorClaro, 0.15f);
        ContenedorTemaClaro.setBackground(colorOscuroFlat);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_ContenedorTemaClaroMouseEntered

    private void txtTemaClaroMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTemaClaroMouseEntered
        Color colorOscuroFlat = ColorFunctions.darken(baseColorClaro, 0.15f);
        ContenedorTemaClaro.setBackground(colorOscuroFlat);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_txtTemaClaroMouseEntered

    private void iconoSolMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconoSolMouseEntered
        Color colorOscuroFlat = ColorFunctions.darken(baseColorClaro, 0.15f);
        ContenedorTemaClaro.setBackground(colorOscuroFlat);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_iconoSolMouseEntered

    private void ContenedorMenuMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContenedorMenuMouseExited
        ContenedorMenu.setBackground(baseColorMenu);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ContenedorMenuMouseExited

    private void txtMenuMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtMenuMouseExited
        ContenedorMenu.setBackground(baseColorMenu);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_txtMenuMouseExited

    private void iconoMenuMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconoMenuMouseExited
        ContenedorMenu.setBackground(baseColorMenu);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_iconoMenuMouseExited

    private void ContenedorTemaOscuroMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContenedorTemaOscuroMouseExited
        ContenedorTemaOscuro.setBackground(baseColorOscuro);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ContenedorTemaOscuroMouseExited

    private void txtTemaOscuroMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTemaOscuroMouseExited
        ContenedorTemaOscuro.setBackground(baseColorOscuro);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_txtTemaOscuroMouseExited

    private void iconoLunaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconoLunaMouseExited
        ContenedorTemaOscuro.setBackground(baseColorOscuro);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_iconoLunaMouseExited

    private void ContenedorTemaClaroMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContenedorTemaClaroMouseExited
        ContenedorTemaClaro.setBackground(baseColorClaro);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_ContenedorTemaClaroMouseExited

    private void txtTemaClaroMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTemaClaroMouseExited
        ContenedorTemaClaro.setBackground(baseColorClaro);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_txtTemaClaroMouseExited

    private void iconoSolMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconoSolMouseExited
        ContenedorTemaClaro.setBackground(baseColorClaro);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_iconoSolMouseExited

    private void ContenedorTemaOscuroMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContenedorTemaOscuroMouseClicked
        cambiarTema(false);
    }//GEN-LAST:event_ContenedorTemaOscuroMouseClicked

    private void txtTemaOscuroMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTemaOscuroMouseClicked
        cambiarTema(false);
    }//GEN-LAST:event_txtTemaOscuroMouseClicked

    private void iconoLunaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconoLunaMouseClicked
        cambiarTema(false);
    }//GEN-LAST:event_iconoLunaMouseClicked

    private void ContenedorTemaClaroMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContenedorTemaClaroMouseClicked
        cambiarTema(true);
    }//GEN-LAST:event_ContenedorTemaClaroMouseClicked

    private void txtTemaClaroMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTemaClaroMouseClicked
        cambiarTema(true);
    }//GEN-LAST:event_txtTemaClaroMouseClicked

    private void iconoSolMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconoSolMouseClicked
        cambiarTema(true);
    }//GEN-LAST:event_iconoSolMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FrameFlatLaf().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ContenedorMenu;
    private javax.swing.JPanel ContenedorPrincipal;
    private javax.swing.JPanel ContenedorTemaClaro;
    private javax.swing.JPanel ContenedorTemaOscuro;
    private javax.swing.JPanel PanelCentral;
    private javax.swing.JPanel PanelInferior;
    private javax.swing.JPanel PanelIzquierdo;
    private javax.swing.JPanel PanelMenu;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel iconoLuna;
    private javax.swing.JLabel iconoMenu;
    private javax.swing.JLabel iconoSol;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel txtMenu;
    private javax.swing.JLabel txtTemaClaro;
    private javax.swing.JLabel txtTemaOscuro;
    // End of variables declaration//GEN-END:variables
}
