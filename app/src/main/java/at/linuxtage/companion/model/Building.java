package at.linuxtage.companion.model;

import android.text.TextUtils;

import at.linuxtage.companion.R;

public enum Building {
    I12("Inffeldgasse 12"), Unknown("Unknown"), I18EG("Inffeldgasse 16a EG"), I25fEG("Inffeldgasse 25f EG"), I10("Inffeldgasse 10"), I25d("Inffeldgasse 25d"), I251OG("Inffeldgasse 25 1. OG"), I12EG("Inffeldgasse 12"), I16bK("Inffeldgasse 16b Keller"), I18("Inffeldgasse 18");
    private final String name;

    Building(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        if (this.equals(Unknown)) {
            //return getString(R.string.unknown);
        }
        return this.name;
    }

    //TODO mapping of Building to room for GLT
    public static Building fromRoomName(String roomName) {
        Building building = Unknown;
        if (!TextUtils.isEmpty(roomName)) {
            switch (roomName) {
                case "HF01092":
                    building = I12;
                    break;

                case "HSTP056J":
                case "i1":
                case "HSEG058J":
                case "IFEG042":
                    building = I18EG;
                    break;

                case "MFEG210":
                    building = I25fEG;
                    break;

                case "SZ01070":
                case "SZ02053":
                    building = I10;
                    break;

                case "i3":
                case "i4":
                case "i5":
                case "i6":
                    building = I25d;
                    break;

                case "i11":
                case "ICK1002H":
                case "i12":
                case "ICK1130H":
                case "i13":
                case "ICK1120H":
                    building = I16bK;
                    break;

                case "i14":
                case "i15":
                    building = I18;
                    break;


                case "i2":
                case "HFEG038J":
                    building = I12EG;
                    break;

                case "i7":
                case "MD01168F":
                    building = I251OG;
                    break;
            }
        }
        return building;
    }
}

