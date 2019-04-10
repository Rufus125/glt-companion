package at.linuxtage.companion.model;

import android.text.TextUtils;

import at.linuxtage.companion.R;

public enum Building {
    Unknown("Unknown"),
    I10("Inffeldgasse 10"),
    I101OG("Inffeldgasse 10 1. OG"),
    I102OG("Inffeldgasse 10 2. OG"),

    I12EG("Inffeldgasse 12 EG"),
    I121OG("Inffeldgasse 12 1. OG"),

    I16aEG("Inffeldgasse 16a EG"),

    I16bK("Inffeldgasse 16b Keller"),

    I18EG("Inffeldgasse 18 EG"),
    I18K("Inffeldgasse 18 Keller"),


    I25dEG("Inffeldgasse 25d EG"),
    I25d1OG("Inffeldgasse 25d 1. OG"),
    I25fEG("Inffeldgasse 25f EG");


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
                case "IFEG042":
                case "ifeg042":
                    building = I16aEG;
                    break;

                case "HF01092":
                case "hf01092":
                    building = I121OG;
                    break;


                case "MFEG210":
                case "mfeg210":
                    building = I25fEG;
                    break;

                case "SZ01070":
                case "sz01070":
                    building = I101OG;
                    break;

                case "SZ02053":
                case "sz02053":
                    building = I102OG;
                    break;

                case "i3":
                case "i4":
                    building = I25dEG;
                    break;
                case "i5":
                case "i6":
                case "i7":
                    building = I25d1OG;
                    break;

                case "i11":
//                case "ICK1002H":
                case "i12":
//                case "ICK1130H":
                case "i13":
//                case "ICK1120H":
                    building = I16bK;
                    break;

                case "i2":
                    building = I12EG;
                    break;

                case "i1":
                    building = I18EG;
                    break;

                case "i14":
                case "i15":
                case "HSTP056J":
                case "hstp056j":
                case "HSTP056":
                case "hstp056":
                    building = I18K;
                    break;
            }
        }
        return building;
    }
}

