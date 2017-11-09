import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class ApiClashRoyale {

    static boolean connection(String tag) throws IOException {
        String link = Constants.APIPROFILE + tag;
        return !getJsonString(link).equals(Constants.INVALIDTAG);
    }

    private static String getJsonString(String link) throws IOException {
        URL url = new URL(link);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        InputStream inputStream;
        try {
            inputStream = urlConnection.getInputStream();
        } catch (IOException e) {
            return Constants.INVALIDTAG;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder stringBuffer = new StringBuilder();

        while (reader.ready())
            stringBuffer.append(reader.readLine());
        reader.close();

        byte[] bytes = stringBuffer.toString().getBytes();

        return new String(bytes, "UTF-8");
    }

    static String getProfile(String tag) throws IOException {
        StringBuilder result = new StringBuilder();
        JSONObject profile = new JSONObject(getJsonString(Constants.APIPROFILE + tag));

        result.append(EmojiCode.getEmoji("prince")).append("Ник: ")
                .append(profile.getString("name")).append("\n");

        result.append(EmojiCode.getEmoji("star2")).append("Уровень: ")
                .append(profile.getJSONObject("experience").getInt("level")).append("\n");

        result.append(EmojiCode.getEmoji("trophy")).append("Трофеи: ")
                .append(profile.getInt("trophies")).append("\n");

        result.append(EmojiCode.getEmoji("classical_building")).append("Арена: ")
                .append(profile.getJSONObject("arena").getString("name")).append("\n");

        if (!profile.get("globalRank").toString().equals("null"))
            result.append(EmojiCode.getEmoji("top")).append("Глобальный рейтинг: ")
                    .append(profile.get("globalRank").toString()).append("\n");

        result.append(EmojiCode.getEmoji("black_joker")).append("Найдено карт: ")
                .append(profile.getJSONObject("stats").getInt("cardsFound")).append("\n");

        result.append(EmojiCode.getEmoji("heart_decoration")).append("Любимая карта: ")
                .append(profile.getJSONObject("stats").getString("favoriteCard")).append("\n");

        result.append(EmojiCode.getEmoji("raised_hands")).append("Пожертвований: ")
                .append(profile.getJSONObject("stats").getInt("totalDonations")).append("\n");

        result.append("\n");

        JSONArray pack = profile.getJSONArray("currentDeck");
        result.append(EmojiCode.getEmoji("muscle")).append("Колода\n");

        for (Object o : pack) {
            JSONObject card = (JSONObject) o;
            result.append(card.getString("name")).append(" ").append(card.getInt("level")).append(" уровня\n");
        }

        return result.toString();
    }

    static String getTrophies(String tag) throws IOException {
        StringBuilder result = new StringBuilder();
        JSONObject profile = new JSONObject(getJsonString(Constants.APIPROFILE + tag));

        result.append(EmojiCode.getEmoji("trophy")).append("Всего трофеев: ")
                .append(profile.getInt("trophies")).append("\n");

        result.append(EmojiCode.getEmoji("first_place_medal")).append("Максимальное количество трофеев: ")
                .append(profile.getJSONObject("stats").getInt("maxTrophies")).append("\n");

        result.append(EmojiCode.getEmoji("military_medal")).append("Легендарных трофеев: ")
                .append(profile.get("legendaryTrophies").toString()).append("\n");

        return result.toString();
    }

    static String getBattles(String tag) throws IOException {
        StringBuilder result = new StringBuilder();
        JSONObject profile = new JSONObject(getJsonString(Constants.APIPROFILE + tag));
        JSONObject games = profile.getJSONObject("games");

        int totalWins = games.getInt("total");
        int wins3 = profile.getJSONObject("stats").getInt("threeCrownWins");
        int wins = games.getInt("wins") + wins3;
        int losses = games.getInt("losses");
        int draws = totalWins - wins - losses;

        result.append(EmojiCode.getEmoji("dagger")).append("Всего боев: ").append(totalWins).append("\n");
        result.append(EmojiCode.getEmoji("first_place_medal")).append("Победы: ").append(wins).append("\n");
        result.append(EmojiCode.getEmoji("horns_sign")).append("Победы в 3 короны: ").append(wins3).append("\n");
        result.append(EmojiCode.getEmoji("waving_white_flag")).append("Поражения: ").append(losses).append("\n");
        result.append(EmojiCode.getEmoji("scales")).append("Ничьи: ").append(draws).append("\n");

        return result.toString();
    }

    static String getChests(String tag) throws IOException {
        StringBuilder result = new StringBuilder();
        JSONObject profile = new JSONObject(getJsonString(Constants.APIPROFILE + tag));
        JSONObject chests = profile.getJSONObject("chestCycle");

        int position = chests.getInt("position");

        result.append(EmojiCode.getEmoji("star2")).append("Эпический через ")
                .append(chests.getInt("epicPos") - position).append(" сундуков\n");

        result.append(EmojiCode.getEmoji("dizzy")).append("СуперМагический через ")
                .append(chests.getInt("superMagicalPos") - position).append(" сундуков\n");

        result.append(EmojiCode.getEmoji("sparkles")).append("Легендарный через ")
                .append(chests.getInt("legendaryPos") - position).append(" сундуков\n");


        return result.toString();
    }

    static String getSeason(String tag) throws IOException {
        StringBuilder result = new StringBuilder();
        JSONObject profile = new JSONObject(getJsonString(Constants.APIPROFILE + tag));

        JSONObject season;
        try {
            season = profile.getJSONArray("previousSeasons").getJSONObject(0);
        } catch (JSONException e) {
            return "Вы пока не состоите в лиге!";
        }

        result.append("Номер сезона: ").append(season.getInt("seasonNumber")).append("\n");
        result.append("Лучший результат: ").append(season.getInt("seasonHighest")).append("\n");
        result.append("Конечный результат: ").append(season.getInt("seasonEnding")).append("\n");
        result.append("Рейтинг: ").append(season.getInt("seasonEndGlobalRank")).append("\n");

        return result.toString();
    }

    static String getClan(String tag) throws IOException {
        StringBuilder result = new StringBuilder();
        JSONObject profile = new JSONObject(getJsonString(Constants.APIPROFILE + tag));

        JSONObject clan;
        try {
            clan = new JSONObject(getJsonString(Constants.APICLAN + profile.getJSONObject("clan").getString("tag")));
        } catch (JSONException e) {
            return "Вы не состоите в клане!";
        }

        result.append(EmojiCode.getEmoji("id")).append("Имя: ")
                .append(clan.getString("name")).append("\n");

        result.append(EmojiCode.getEmoji("man_judge")).append("Участников: ")
                .append(clan.getInt("memberCount")).append("\n");

        result.append(EmojiCode.getEmoji("100")).append("Очков: ")
                .append(clan.getInt("score")).append("\n");

        result.append(EmojiCode.getEmoji("raised_hands")).append("Пожертвований: ")
                .append(clan.getInt("donations")).append("\n");

        result.append(EmojiCode.getEmoji("fleur_de_lis")).append("Клановый сундук набит на ").
                append(clan.getJSONObject("clanChest").getInt("clanChestCrowns")).append(" корон");

        return result.toString();
    }
}
