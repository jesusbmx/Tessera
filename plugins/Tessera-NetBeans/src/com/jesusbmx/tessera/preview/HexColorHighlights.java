package com.jesusbmx.tessera.preview;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.spi.editor.highlighting.HighlightsLayer;
import org.netbeans.spi.editor.highlighting.HighlightsLayerFactory;
import org.netbeans.spi.editor.highlighting.HighlightsSequence;
import org.netbeans.spi.editor.highlighting.ZOrder;
import org.netbeans.spi.editor.highlighting.support.AbstractHighlightsContainer;

/**
 * Pinta con su propio color los colores hexadecimales que aparecen escritos en el editor ({@code "#1976D2"}): el fondo del
 * texto es ese color y las letras blanco o negro segun cual se lea mejor. Longitudes: {@code #RGB}, {@code #RGBA},
 * {@code #RRGGBB} y {@code #RRGGBBAA} (la transparencia se mezcla con el fondo del editor).
 *
 * Es una capa de resaltado del editor (API publica de NetBeans) registrada en layer.xml para los archivos .java; el
 * resaltado se calcula sobre el texto visible, asi que no cuesta nada en archivos grandes y sigue lo que se escribe.
 */
public final class HexColorHighlights implements HighlightsLayerFactory {

  /** No es parte de otra palabra ni de otro hexadecimal mas largo: {@code #RGB}, {@code #RGBA}, {@code #RRGGBB}, {@code #RRGGBBAA}. */
  private static final Pattern HEX = Pattern.compile("(?<![\\w#])#(?:[0-9A-Fa-f]{8}|[0-9A-Fa-f]{6}|[0-9A-Fa-f]{3,4})(?!\\w)");
  /** Un color mas largo que esto no cabe: se lee un poco mas alla del rango pedido para no partir uno por la mitad. */
  private static final int MARGIN = 10;

  @Override
  public HighlightsLayer[] createLayers(Context context) {
    return new HighlightsLayer[] {
      HighlightsLayer.create("tessera-hex-colors", ZOrder.SYNTAX_RACK.forPosition(2000), true,
          new Container(context.getDocument(), context.getComponent()))
    };
  }

  private static final class Container extends AbstractHighlightsContainer implements DocumentListener {

    private final Document document;
    private final JTextComponent component;

    Container(Document document, JTextComponent component) {
      this.document = document;
      this.component = component;
      document.addDocumentListener(this);
    }

    @Override
    public HighlightsSequence getHighlights(int start, int end) {
      final List<int[]> ranges = new ArrayList<>();
      final List<AttributeSet> attributes = new ArrayList<>();
      final int from = Math.max(0, start - MARGIN);
      final int to = Math.min(document.getLength(), end + MARGIN);
      if (to <= from) {
        return HighlightsSequence.EMPTY;
      }
      final String[] text = new String[1];
      final Runnable read = () -> {
        try {
          text[0] = document.getText(from, to - from);
        } catch (BadLocationException e) {
          text[0] = null;
        }
      };
      if (document instanceof AbstractDocument) {
        ((AbstractDocument) document).render(read);
      } else {
        read.run();
      }
      if (text[0] == null) {
        return HighlightsSequence.EMPTY;
      }
      final Color background = component == null ? Color.WHITE : component.getBackground();
      final Matcher matcher = HEX.matcher(text[0]);
      while (matcher.find()) {
        final int matchStart = Math.max(start, from + matcher.start());
        final int matchEnd = Math.min(end, from + matcher.end());
        if (matchStart < matchEnd) {
          ranges.add(new int[] {matchStart, matchEnd});
          attributes.add(paint(matcher.group().substring(1), background));
        }
      }
      return new Sequence(ranges, attributes);
    }

    /** Fondo del color y letras blanco o negro; la transparencia (si trae) se mezcla con el fondo del editor. */
    private static AttributeSet paint(String digits, Color editor) {
      final String full;
      switch (digits.length()) {
        case 3:
        case 4:
          final StringBuilder expanded = new StringBuilder();
          for (char c : digits.toCharArray()) {
            expanded.append(c).append(c);
          }
          full = expanded.toString();
          break;
        default:
          full = digits;
      }
      final int red = Integer.parseInt(full.substring(0, 2), 16);
      final int green = Integer.parseInt(full.substring(2, 4), 16);
      final int blue = Integer.parseInt(full.substring(4, 6), 16);
      final int alpha = full.length() == 8 ? Integer.parseInt(full.substring(6, 8), 16) : 255;
      final Color color = new Color(blend(red, editor.getRed(), alpha), blend(green, editor.getGreen(), alpha),
          blend(blue, editor.getBlue(), alpha));
      final double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
      return AttributesUtilities.createImmutable(StyleConstants.Background, color,
          StyleConstants.Foreground, luminance > 0.55 ? Color.BLACK : Color.WHITE);
    }

    private static int blend(int color, int under, int alpha) {
      return (color * alpha + under * (255 - alpha)) / 255;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
      changed(e.getOffset());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      changed(e.getOffset());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    /** Lo que sigue a un cambio se desplaza: se pide repintar desde un poco antes hasta el final. */
    private void changed(int offset) {
      fireHighlightsChange(Math.max(0, offset - MARGIN), Math.max(offset, document.getLength()));
    }
  }

  /** Los resaltados ya calculados, en orden. */
  private static final class Sequence implements HighlightsSequence {

    private final List<int[]> ranges;
    private final List<AttributeSet> attributes;
    private int index = -1;

    Sequence(List<int[]> ranges, List<AttributeSet> attributes) {
      this.ranges = ranges;
      this.attributes = attributes;
    }

    @Override
    public boolean moveNext() {
      return ++index < ranges.size();
    }

    @Override
    public int getStartOffset() {
      return ranges.get(index)[0];
    }

    @Override
    public int getEndOffset() {
      return ranges.get(index)[1];
    }

    @Override
    public AttributeSet getAttributes() {
      return attributes.get(index);
    }
  }
}
