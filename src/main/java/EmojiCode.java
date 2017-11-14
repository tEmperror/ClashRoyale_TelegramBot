import com.vdurmont.emoji.EmojiManager;

class EmojiCode {

    /**
     * @param emoji - Имя Emoji
     * @return Unicode
     */
    static String getEmoji(String emoji) {
        return EmojiManager.getForAlias(emoji).getUnicode();
    }
}
