package com.apachetune.httpserver.ui.about;

import com.apachetune.core.AppVersion;
import com.apachetune.core.WorkItem;
import com.apachetune.core.ui.SmartPart;
import com.google.inject.Inject;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXHyperlink;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.apachetune.core.utils.Utils.createRuntimeException;
import static org.apache.commons.lang.Validate.notNull;

public class AboutSmartPart extends JDialog implements AboutDialog, SmartPart {
    private JPanel contentPane;

    private JLabel copyrightLabel;
    private JLabel fullProductNameLabel;
    private JLabel buildDateLabel;
    private JLabel ownerLabel;
    private JLabel vendorLabel;
    private JXHyperlink webSiteLabel;
    private JLabel imageLabel;
    private JLabel buildDateTitleLabel;
    private JLabel ownerTitleLabel;
    private JLabel vendorTitleLabel;
    private JLabel productNameImageLabel;

    private String productName;

    private AppVersion productVersion;

    @Inject
    public AboutSmartPart(JFrame mainFrame) {
        super(mainFrame);

        setContentPane(contentPane);
        setModal(true);

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // TODO DO_NOTHING_ON_CLOSE
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        }
        );

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );

        webSiteLabel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onWebSiteLabelClicked();
            }
        }
        );

        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        }
        );
        contentPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        }
        );
        fullProductNameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        }
        );
        buildDateTitleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        }
        );
        buildDateLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        }
        );
        ownerTitleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        }
        );
        ownerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        }
        );
        vendorTitleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        }
        );
        vendorLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        }
        );
        copyrightLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        }
        );
        productNameImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
        }
        );
    }

    public void initialize(WorkItem workItem) {
        notNull(workItem, "Argument workItem cannot be a null [this = " + this + "]");

        setUndecorated(true);

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        getRootPane().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        }, escapeKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        pack();

        setLocationRelativeTo(getParent());

        //GridLayoutManager layout = getLayout();
    }

    public void run() {
        setVisible(true);
    }

    public void setProductName(String name) {
        productName = name;

        updateProductFullName();
    }

    public void setProductVersion(AppVersion version) {
        productVersion = version;

        updateProductFullName();
    }

    public void setProductBuildDate(Date buildDate) {
        buildDateLabel.setText(new SimpleDateFormat("dd MMMM yyyy").format(buildDate));
    }

    public void setProductVendor(String vendor) {
        vendorLabel.setText(vendor);
    }

    public void setProductWebSite(URL webSite) {
        // TODO Make it with a hyperlink.
        webSiteLabel.setText(webSite.toString());
    }

    public void setProductCopyrightText(String copyrightText) {
        copyrightLabel.setText(copyrightText);
    }

    public void setProductOwner(String owner) {
        ownerLabel.setText(owner);
    }

    private void updateProductFullName() {
        fullProductNameLabel.setText(StringUtils.defaultString(productName) + ((productVersion != null) ? ' ' +
                productVersion.format() : "")
        );
    }

    private void onCancel() {
        dispose();
    }

    // TODO Move to presenter.

    private void onWebSiteLabelClicked() {
        try {
            Desktop.getDesktop().browse(new URI(webSiteLabel.getText()));
        } catch (IOException e) {
            throw createRuntimeException(e);
        } catch (URISyntaxException e) {
            throw createRuntimeException(e);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(11, 20, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.setBackground(new Color(-1));
        contentPane.setMaximumSize(new Dimension(400, 2147483647));
        contentPane.setMinimumSize(new Dimension(400, 149));
        contentPane.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-3355444)), null)
        );
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(0);
        imageLabel.setIcon(new ImageIcon(getClass().getResource(
                "/com/apachetune/httpserver/ui/smartparts/about/light_version_about_dialog_image.png"
        )
        )
        );
        imageLabel.setIconTextGap(0);
        imageLabel.setText("");
        imageLabel.setVerticalAlignment(1);
        contentPane.add(imageLabel,
                        new GridConstraints(0, 0, 1, 10, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                                            null, null, 0, false
                        )
        );
        buildDateTitleLabel = new JLabel();
        buildDateTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        buildDateTitleLabel.setText("Built on");
        contentPane.add(buildDateTitleLabel,
                        new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                                            null, null, 5, false
                        )
        );
        ownerTitleLabel = new JLabel();
        ownerTitleLabel.setFocusable(false);
        ownerTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        ownerTitleLabel.setText("Licensed to");
        contentPane.add(ownerTitleLabel,
                        new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                                            null, null, 5, false
                        )
        );
        vendorLabel = new JLabel();
        vendorLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        vendorLabel.setMaximumSize(new Dimension(120, 15));
        vendorLabel.setMinimumSize(new Dimension(120, 15));
        vendorLabel.setPreferredSize(new Dimension(120, 15));
        vendorLabel.setText("<vendor>");
        contentPane.add(vendorLabel,
                        new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                                            null, null, 6, false
                        )
        );
        fullProductNameLabel = new JLabel();
        fullProductNameLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        fullProductNameLabel.setText("<fullProductName>");
        contentPane.add(fullProductNameLabel,
                        new GridConstraints(2, 0, 1, 10, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                                            null, null, 5, false
                        )
        );
        vendorTitleLabel = new JLabel();
        vendorTitleLabel.setText("Vendor:");
        contentPane.add(vendorTitleLabel,
                        new GridConstraints(7, 0, 1, 10, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                                            null, null, 5, false
                        )
        );
        buildDateLabel = new JLabel();
        buildDateLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        buildDateLabel.setText("<buildDate>");
        contentPane.add(buildDateLabel,
                        new GridConstraints(3, 1, 1, 8, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                            GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null,
                                            null, null, 0, false
                        )
        );
        ownerLabel = new JLabel();
        ownerLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        ownerLabel.setText("<owner>");
        contentPane.add(ownerLabel,
                        new GridConstraints(5, 1, 1, 8, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                            GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null,
                                            null, null, 0, false
                        )
        );
        copyrightLabel = new JLabel();
        copyrightLabel.setBackground(new Color(-1250856));
        copyrightLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));
        copyrightLabel.setForeground(new Color(-16777216));
        copyrightLabel.setOpaque(false);
        copyrightLabel.setText("<copyright>");
        contentPane.add(copyrightLabel,
                        new GridConstraints(10, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                            GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                            null, null, null, 1, false
                        )
        );
        webSiteLabel = new JXHyperlink();
        webSiteLabel.setClickedColor(new Color(-16763905));
        webSiteLabel.setFocusable(false);
        webSiteLabel.setText("<productWebSite>");
        contentPane.add(webSiteLabel,
                        new GridConstraints(8, 1, 1, 8, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                            GridConstraints.SIZEPOLICY_CAN_GROW,
                                            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                            null, null, null, 0, false
                        )
        );
        final Spacer spacer1 = new Spacer();
        contentPane.add(spacer1,
                        new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1,
                                            GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10),
                                            new Dimension(-1, 10), new Dimension(-1, 10), 0, false
                        )
        );
        final Spacer spacer2 = new Spacer();
        contentPane.add(spacer2,
                        new GridConstraints(4, 9, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                            GridConstraints.SIZEPOLICY_WANT_GROW, 1, new Dimension(10, -1),
                                            new Dimension(10, -1), new Dimension(10, -1), 0, false
                        )
        );
        final Spacer spacer3 = new Spacer();
        contentPane.add(spacer3,
                        new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1,
                                            GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10),
                                            new Dimension(-1, 10), new Dimension(-1, 10), 0, false
                        )
        );
        final Spacer spacer4 = new Spacer();
        contentPane.add(spacer4,
                        new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1,
                                            GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 10),
                                            new Dimension(-1, 10), new Dimension(-1, 10), 0, false
                        )
        );
        productNameImageLabel = new JLabel();
        productNameImageLabel.setIcon(new ImageIcon(
                getClass().getResource("/com/apachetune/httpserver/ui/smartparts/about/product_name_as_mini_image.png")
        )
        );
        productNameImageLabel.setText("");
        contentPane.add(productNameImageLabel,
                        new GridConstraints(10, 6, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                                            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                                            new Dimension(83, -1), new Dimension(83, -1), new Dimension(83, -1), 0,
                                            false
                        )
        );
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
