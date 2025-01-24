package com.codigodebarra.controller;

import com.codigodebarra.config.Disenio;
import com.codigodebarra.dao.ProductoDao;
import com.codigodebarra.dao.daoimpl.ProductoDaoImpl;
import com.codigodebarra.model.Producto;
import com.codigodebarra.model.Usuario;
import com.codigodebarra.util.ApiProductos;
import com.codigodebarra.util.Barras;
import com.codigodebarra.view.JInformacion;
import com.codigodebarra.view.JInterfazPrincipal;
import com.codigodebarra.view.JLogin;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public final class PrincipalController implements ActionListener {

    JInterfazPrincipal vistaIp;
    JInformacion vistaInfo;
    ProductoDao productoDao;
    ApiProductos api;
    Producto productoGlobal;
    Usuario usuario;
    DefaultTableModel modelo;

    int xMouse, yMouse;

    public PrincipalController(JInterfazPrincipal vistaIp, Usuario usuario) {
        this.vistaIp = vistaIp;
        //Se necesita que la vistaIp ya haya sida creada
        bienvenida(usuario);
        this.vistaIp.setVisible(true);
        this.vistaIp.setLocationRelativeTo(null);

        vistaInfo = new JInformacion(vistaIp, true);
        productoDao = new ProductoDaoImpl();
        api = new ApiProductos();
        Disenio.getDesignWindows();
        modelo = new DefaultTableModel();
        acciones();

    }

    public void acciones() {
        //Configuración en el TabbedPane y en el la barra para poder deslizar la ventana, e incluso la adioión de paneles
        configuracionTabbedPane();
        //Menu lateral - > paneles
        navegacionTabbedPane();
        //Tabla
        disenioTabla();
        //Boton en info al encontrar el producto
        pnlAgregarOActualizaProducto();
        //Agregar producto
        gestionProductoIp();
        vistaIp.getBtnOkEscanear().addActionListener(this);
        vistaInfo.getBtnCancelar().addActionListener(this);

    }

    public void bienvenida(Usuario usuario) {
        vistaIp.getLblNombre().setText(String.format("%s, %s", usuario.getApellido(), usuario.getNombre()));
    }

    public void mostrarElementosEnTabla(List<Producto> listaProductos) {
        modelo.setRowCount(0);
        for (Producto p : listaProductos) {
            Object[] fila = {p.getCodigo_barra(), p.getNombre(), p.getMarca(), p.getContenido(), p.getPrecio(), p.getCantidad()};
            modelo.addRow(fila);
        }
        vistaIp.getTxtValorProd().setText("");
    }

    private void disenioTabla() {

        String[] tituloColumnas = {"Cod. Barra", "Nombre", "Marca", "Contenido", "Precio", "Cantidad"};

        //Poner nombre a las columnas de la tabla
        modelo.setColumnIdentifiers(tituloColumnas);

        vistaIp.getTablaProductos().setModel(modelo);

        vistaIp.getCbNombresColumnasProd().addItem("<Seleccione un campo>");
        //Ponemos los nombres de las columnas, al combobox para hacer busquedas
        for (String i : tituloColumnas) {
            vistaIp.getCbNombresColumnasProd().addItem(i);
        }

        //=============
        //Agregar elementos de la bd, a la tabla
        List<Producto> productos = productoDao.selectAll();

        mostrarElementosEnTabla(productos);
        //=============
        informacionBusqueda(productos);
    }

    public void informacionBusqueda(List<Producto> productos) {
        vistaIp.getPnlBuscarProd().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int nombreColumna = vistaIp.getCbNombresColumnasProd().getSelectedIndex();

                switch (nombreColumna) {
                    case 0 -> {
                        JOptionPane.showMessageDialog(null, "Seleccione un campo que sea valido");
                        mostrarElementosEnTabla(productos);
                    }
                    case 1 -> {
                        Producto p = null;
                        if (vistaIp.getTxtValorProd().getText() == null) {
                            modelo.setNumRows(0);
                        } else {
                            p = productoDao.findByCodeProduct(vistaIp.getTxtValorProd().getText());
                            modelo.setNumRows(0);
                            Object[] fila = {p.getCodigo_barra(), p.getNombre(), p.getMarca(), p.getContenido(), p.getPrecio(), p.getCantidad()};
                            modelo.addRow(fila);
                        }
                    }
                    case 2 -> {
                        List<Producto> listaProductos = productoDao.findByName(vistaIp.getTxtValorProd().getText());
                        mostrarElementosEnTabla(listaProductos);
                    }
                    case 3 -> {
                        List<Producto> listaProductos = productoDao.findByBrand(vistaIp.getTxtValorProd().getText());
                        mostrarElementosEnTabla(listaProductos);
                    }
                    case 4 -> {
                        List<Producto> listaProductos = productoDao.findByContent(vistaIp.getTxtValorProd().getText());
                        mostrarElementosEnTabla(listaProductos);
                    }
                    case 5 -> {
                        List<Producto> listaProductos = productoDao.findByPrice(vistaIp.getTxtValorProd().getText());
                        mostrarElementosEnTabla(listaProductos);
                    }
                    case 6 -> {
                        List<Producto> listaProductos = productoDao.findByQuantity(vistaIp.getTxtValorProd().getText());
                        mostrarElementosEnTabla(listaProductos);
                    }
                    default -> {
                        mostrarElementosEnTabla(productos);
                    }
                }
            }
        });
    }

    public void alternarPanelesGP(boolean valor) {
        vistaIp.getPnlAgregarGP().setEnabled(valor);
        vistaIp.getPnlActualizarGP().setEnabled(!valor);
        vistaIp.getPnlEliminarGP().setEnabled(!valor);
        vistaIp.getPnlLimpiarGP().setEnabled(!valor);
        if (valor == true) {
            vistaIp.getPnlAgregarGP().setBackground(new Color(255, 255, 255));
            vistaIp.getPnlActualizarGP().setBackground(new Color(200, 200, 200));
            vistaIp.getPnlEliminarGP().setBackground(new Color(200, 200, 200));
            vistaIp.getPnlLimpiarGP().setBackground(new Color(200, 200, 200));
            limpiezaTextFieldsGP();
        } else {
            vistaIp.getPnlAgregarGP().setBackground(new Color(200, 200, 200));
            vistaIp.getPnlActualizarGP().setBackground(new Color(255, 255, 255));
            vistaIp.getPnlEliminarGP().setBackground(new Color(255, 255, 255));
            vistaIp.getPnlLimpiarGP().setBackground(new Color(255, 255, 255));
        }
    }

    public void gestionProductoIp() {
        //Por default, los paneles en la gestión de producto deben estar así
        alternarPanelesGP(true);
        vistaIp.getTablaProductos().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            Producto pr = null;

            //Se ejecutará cuando escuche un cambio en la tabla
            @Override
            public void valueChanged(ListSelectionEvent e) {

                if (!e.getValueIsAdjusting()) {

                    int fila = vistaIp.getTablaProductos().getSelectedRow();

                    //Si en caso selecciona una fila
                    if (fila != -1) {
                        alternarPanelesGP(false);
                        String codBarra = (String) vistaIp.getTablaProductos().getValueAt(fila, 0);
                        String nombre = (String) vistaIp.getTablaProductos().getValueAt(fila, 1);
                        String marca = (String) vistaIp.getTablaProductos().getValueAt(fila, 2);
                        String contenido = (String) vistaIp.getTablaProductos().getValueAt(fila, 3);
                        double precio = (double) vistaIp.getTablaProductos().getValueAt(fila, 4);
                        int cantidad = (int) vistaIp.getTablaProductos().getValueAt(fila, 5);

                        pr = new Producto();
                        //Mostrar valores en los textFields
                        vistaIp.getTxtCodBarraGP().setText(codBarra);
                        vistaIp.getTxtNombreGP().setText(nombre);
                        vistaIp.getTxtMarcaGP().setText(marca);
                        vistaIp.getTxtContenidoGP().setText(contenido);
                        vistaIp.getSpnPrecioGP().setValue(precio);
                        vistaIp.getSpnCantidadGP().setValue(cantidad);

                        //Metodos accion
                        //Eliminar
                        vistaIp.getPnlEliminarGP().addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent ev) {
                                int fila = vistaIp.getTablaProductos().getSelectedRow();
                                if (fila != -1) {
                                    modelo.removeRow(fila);
                                }
                            }
                        });
                        vistaIp.getPnlLimpiarGP().addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent ev) {
                                limpiezaTextFieldsGP();
                            }
                        });
                        //Si en caso se seleccione una fila
                    } else {
                        alternarPanelesGP(true);

                        //Agregar producto
                        vistaIp.getPnlAgregarGP().addMouseListener(new MouseAdapter() {

                            @Override
                            public void mouseClicked(MouseEvent ev) {
                                String codigoB = vistaIp.getTxtCodBarraGP().getText();
                                if (productoDao.findByCodeProduct(codigoB) != null && !codigoB.equals("")) {
                                    String nombreP = vistaIp.getTxtNombreGP().getText();
                                    String marcaP = vistaIp.getTxtMarcaGP().getText();
                                    String contenidoP = vistaIp.getTxtContenidoGP().getText();
                                    double precioP = ((Number) vistaIp.getSpnPrecioGP().getValue()).doubleValue();
                                    int cantidadP = (int) vistaIp.getSpnCantidadGP().getValue();

                                    Producto p = new Producto();
                                    p.setCodigo_barra(codigoB);
                                    p.setNombre(nombreP);
                                    p.setMarca(marcaP);
                                    p.setContenido(contenidoP);
                                    p.setPrecio(precioP);
                                    p.setCantidad(cantidadP);

                                    if (productoDao.insert(p) != 0) {
                                        System.out.println("Se agregó exitosamente");
                                    } else {
                                        System.out.println("Error en la inserción del producto, desde GP");
                                    }
                                    List<Producto> productos = productoDao.selectAll();
                                    mostrarElementosEnTabla(productos);
                                } else {
                                    JOptionPane.showMessageDialog(null, "No se aceptan códigos de barras duplicados");
                                }
                                limpiezaTextFieldsGP();
                            }
                        });
                    }
                }
            }
        });
    }

    public void limpiezaTextFieldsGP() {
        vistaIp.getTxtCodBarraGP().setText("");
        vistaIp.getTxtNombreGP().setText("");
        vistaIp.getTxtMarcaGP().setText("");
        vistaIp.getTxtContenidoGP().setText("");
        vistaIp.getSpnPrecioGP().setValue(0);
        vistaIp.getSpnCantidadGP().setValue(0);
    }

    public void pnlAgregarOActualizaProducto() {
        vistaInfo.getBtnAceptar().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //Ayudará a separar la lógica del botón
                String command = e.getActionCommand();
                if (command.equals("aumentar")) {
                    System.out.println("Se va a aumentar");
                    productoDao.updateQuantityAfterInsert(productoGlobal.getId());

                    //Cambiar el TextArea por elementos gráficos separados, dentro de un panel
                    //vistaInfo.getTxtAreaInformacion().setText(textAreaInfo.toString());
                    System.out.println("El id es: " + productoGlobal.getId());
                } else if (command.equals("agregar")) {
                    System.out.println("Se va a agregar");
                    int idObtenido = productoDao.insert(productoGlobal);
                    if (idObtenido > 0) {
                        System.out.println("Se agregó el producto, mira la bd");
                        productoDao.updateQuantityAfterInsert(idObtenido);
                        vistaInfo.dispose();

                    } else {
                        System.out.println("No se agregó");
                    }
                }
            }
        });
    }

    private void configuracionTabbedPane() {
        //Ocultar las pestañas del TabbedPane
        vistaIp.getjTabbedPane1().setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int runCount, int maxTabHeight) {
                return 0; // Ocultar las pestañas
            }
        });
        //Barra superior
        vistaIp.getPnlBarraDeOpciones().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                xMouse = event.getX();
                yMouse = event.getY();
            }
        });

        vistaIp.getPnlBarraDeOpciones().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                int x = event.getXOnScreen();
                int y = event.getYOnScreen();

                vistaIp.setLocation(x - xMouse, y - yMouse);
            }
        });

        vistaIp.getPnlXLogin().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int respuesta = JOptionPane.showConfirmDialog(null, "¿Deseas cerrar sesión?");
                if (respuesta == 0) {
                    vistaIp.dispose();
                    JLogin login = new JLogin();
                    LoginController lc = new LoginController(login);
                }
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                vistaIp.getPnlXLogin().setBackground(new Color(254, 57, 57));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                vistaIp.getPnlXLogin().setBackground(new Color(51, 51, 51));
            }
        });

        vistaIp.getPnlFSLogin().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (vistaIp.getExtendedState() != vistaIp.MAXIMIZED_BOTH) {
                    vistaIp.setExtendedState(vistaIp.MAXIMIZED_BOTH);
//                  vistaLogin.getPnlAll().setMaximumSize(vistaLogin.getMaximumSize());
                } else {
                    vistaIp.setExtendedState(vistaIp.NORMAL);
                }
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                vistaIp.getPnlFSLogin().setBackground(new Color(95, 92, 93));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                vistaIp.getPnlFSLogin().setBackground(new Color(51, 51, 51));
            }
        });

        vistaIp.getPnlMinusLogin().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if ((vistaIp.getExtendedState() == vistaIp.NORMAL) || (vistaIp.getExtendedState() == vistaIp.MAXIMIZED_BOTH)) {
                    vistaIp.setExtendedState(vistaIp.HIDE_ON_CLOSE);
                }
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                vistaIp.getPnlMinusLogin().setBackground(new Color(95, 92, 93));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                vistaIp.getPnlMinusLogin().setBackground(new Color(51, 51, 51));
            }

        });
    }

    private void navegacionTabbedPane() {
        vistaIp.getPnlPrincipal().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                vistaIp.getjTabbedPane1().setSelectedIndex(0);
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                vistaIp.getPnlPrincipal().setBackground(new Color(220, 220, 220));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                vistaIp.getPnlPrincipal().setBackground(new Color(255, 255, 255));
            }
        });
        vistaIp.getPnlEscanear().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                vistaIp.getjTabbedPane1().setSelectedIndex(1);
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                vistaIp.getPnlEscanear().setBackground(new Color(220, 220, 220));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                vistaIp.getPnlEscanear().setBackground(new Color(255, 255, 255));
            }
        });

        vistaIp.getPnlInventario().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                vistaIp.getjTabbedPane1().setSelectedIndex(2);
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                vistaIp.getPnlInventario().setBackground(new Color(220, 220, 220));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                vistaIp.getPnlInventario().setBackground(new Color(255, 255, 255));
            }
        });
        vistaIp.getPnlCerrarSesion().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {

                int respuesta = JOptionPane.showConfirmDialog(null, "¿Deseas cerrar sesión?");
                /**
                 * -1: Clic en la x | 0: Si | 1: Cancelar | 2: No
                 */
                if (respuesta == 0) {
                    vistaIp.dispose();
                    JLogin login = new JLogin();
                    LoginController lc = new LoginController(login);
                }
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                vistaIp.getPnlCerrarSesion().setBackground(new Color(220, 220, 220));
            }

            @Override
            public void mouseExited(MouseEvent event) {
                vistaIp.getPnlCerrarSesion().setBackground(new Color(255, 255, 255));
            }
        });
    }

    public void escanearCodigo() throws MalformedURLException {
        long startTime = System.currentTimeMillis();

        //Traido de la api, o más rápido de la caché
        Producto productoApi = api.consumirApi(vistaIp.getTxtCodigoEscanear().getText());
        long endTime = System.currentTimeMillis();
        System.out.println("Tiempo de consulta a la API: " + (endTime - startTime) + " ms");
        //Pero la información más detallada, se traerá de la bd
        //long startTimeA = System.currentTimeMillis();
        Producto producto_bd = productoDao.findByCodeProduct(vistaIp.getTxtCodigoEscanear().getText());
        //long endTimeA = System.currentTimeMillis();
        //System.out.println("Tiempo de consulta a la BD: " + (endTimeA - startTimeA) + " ms");

        if (productoApi.getCodigo_barra() != null) {

            System.out.println("Funciono la api");
            //Corregir porque a la primera llamada aparecen los text field con --
            if (productoApi.getNombre().isEmpty()) {

                if (producto_bd == null) {
                    vistaInfo.getTxtNombreProd().setEditable(true);
                    vistaInfo.getTxtNombreProd().setText("--");
                } else {
                    vistaInfo.getTxtNombreProd().setEditable(false);
                    vistaInfo.getTxtNombreProd().setText(producto_bd.getNombre());
                }
            } else {
                vistaInfo.getTxtNombreProd().setEditable(false);

                if (producto_bd == null) {
                    vistaInfo.getTxtNombreProd().setText(productoApi.getNombre());
                } else {
                    //Si el producto existe en la api pero, hice alguna actualizacion entonces traeme de la bd
                    vistaInfo.getTxtNombreProd().setText(producto_bd.getNombre());
                }
            }

            //==========================================
            //Si en caso el producto no retorna el nombre de la compania
            if (productoApi.getMarca().isEmpty()) {
                if (producto_bd == null) {
                    vistaInfo.getTxtCompaniaProd().setEditable(true);
                    vistaInfo.getTxtCompaniaProd().setText("--");
                } else {
                    vistaInfo.getTxtCompaniaProd().setEditable(false);
                    vistaInfo.getTxtCompaniaProd().setText(producto_bd.getNombre());
                }
            } else {
                //Si en caso se haga cambios al resultado dado de la API
                vistaInfo.getTxtCompaniaProd().setEditable(false);
                if (producto_bd == null) {
                    vistaInfo.getTxtCompaniaProd().setText(productoApi.getMarca());
                } else {
                    vistaInfo.getTxtCompaniaProd().setText(producto_bd.getMarca());
                }
            }

            if (productoApi.getContenido().isEmpty()) {

                if (producto_bd == null) {
                    vistaInfo.getTxtContenidoProd().setEditable(true);
                    vistaInfo.getTxtContenidoProd().setText("--");
                } else {
                    vistaInfo.getTxtContenidoProd().setEditable(false);
                    vistaInfo.getTxtContenidoProd().setText(producto_bd.getContenido());
                }
            } else {
                vistaInfo.getTxtContenidoProd().setEditable(false);
                if (producto_bd == null) {
                    vistaInfo.getTxtContenidoProd().setText(productoApi.getContenido());
                } else {
                    vistaInfo.getTxtContenidoProd().setText(producto_bd.getContenido());
                }
            }
            //Esto es así porque no se hace una consulta directa a la API para obtener la cantidad del producto (stock)
            vistaInfo.getTxtCantidadProd().setEditable(false);
            if (producto_bd == null) {
                vistaInfo.getTxtCantidadProd().setText("0");
            } else {
                vistaInfo.getTxtCantidadProd().setText(String.valueOf(producto_bd.getCantidad()));
            }

            //vistaInfo.getTxtAreaInformacion().setText(sb.toString());
            //Almacenamos en la variable global para ver los cambios al actualizar
            //this.textAreaInfo = sb;
            if (productoApi.getImagenURL() != null) {
                if (producto_bd == null) {
                    cargarImagenPorURL(productoApi.getImagenURL());
                } else {
                    cargarImagenPorURL(producto_bd.getImagenURL());
                }
            } else {
                if (producto_bd == null) {
                    vistaInfo.getLblImagen().setText("Imagen no encontrada");
                } else {
                    vistaInfo.getLblImagen().setText(producto_bd.getImagenURL());
                }
            }

            //Se va a consultar en el daoProducto para ver si existe o no en la base de datos
            //Separar la lógica si existe o no el producto en la base de datos
            if (producto_bd != null) {
                System.out.println("Si existe en la base de datos");
                vistaInfo.getLblPreguntar().setText("El producto ya está almacenado. ¿Desea aumentar su cantidad?");
                vistaInfo.getBtnAceptar().setActionCommand("aumentar");
                this.productoGlobal = producto_bd;
            } else {
                System.out.println("No existe en la base de datos");
                vistaInfo.getLblPreguntar().setText("El producto es nuevo. ¿Desea agregarlo?");
                vistaInfo.getBtnAceptar().setActionCommand("agregar");
                this.productoGlobal = productoApi;
            }
            vistaInfo.getLblCodigoBarra().setText(productoApi.getCodigo_barra());
            vistaInfo.setLocationRelativeTo(null);
            vistaInfo.setVisible(true);

        } else {
            JOptionPane.showMessageDialog(null, "No se encontró el producto");
            System.out.println("No funciono la api");
        }

        vistaIp.getTxtCodigoEscanear().setText("");
    }

    public void cargarImagenPorURL(String url_imagen) throws MalformedURLException {
        URL url = new URL(url_imagen);

        // Cargar la imagen desde la URL
        ImageIcon imagen = new ImageIcon(url);

        // Redimensionar la imagen
        Image img = imagen.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        //Volver a crear la imagen pero con la escala actualizada
        imagen = new ImageIcon(img);

        // Asignar la imagen al JLabel
        vistaInfo.getLblImagen().setIcon(imagen);
        vistaInfo.getLblImagen().setText("");
    }

    public void obtenerPDF() {
        Barras ba = new Barras();
        List<Producto> productos = productoDao.selectAll();
        System.out.println(productos);
        for (Producto ps : productos) {
            ba.generarCodBarras(ps.getCodigo_barra(), "EAN-13");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaIp.getBtnOkEscanear()) {

            try {
                escanearCodigo();
            } catch (MalformedURLException ex) {
                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //
        if (e.getSource() == vistaInfo.getBtnCancelar()) {
            vistaInfo.dispose();
        }
    }

}
