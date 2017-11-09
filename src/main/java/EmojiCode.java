import com.vdurmont.emoji.EmojiManager;

class EmojiCode {

    static String getEmoji(String emoji) {
        return EmojiManager.getForAlias(emoji).getUnicode();
    }
}
