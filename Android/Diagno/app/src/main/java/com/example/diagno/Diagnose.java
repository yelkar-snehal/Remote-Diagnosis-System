package com.example.diagno;

public class Diagnose
{

    public String vatabal = "Vibrant, enthusiastic, energetic, Quick and acute responses, Clear mind and alert";
    public String vataimbal = "Restless,anxious, fear, unsettled, Interrupted sleep, Tendency overexert, fatigue to gain, Intolerance of cold";
    public String vatadueto = "Irregular routine, Staying up late, Cold dry weather. Excessive mental work";
    public String vatahowtobal = "Regualr routine, Early bedtime, Warm cooked foods, Abhyanga";

    public String pittabal = "Strong digestion, Good power of concentration, articulate and precise speech, bold and sharpness";
    public String pittaimbal = "Tendency towards anger, hatred, envy, frustration, tendency towards skin rashes, Heartburn";
    public String pittadueto = "Excessive eating and sun, alcohol, smoking, drugs, excessive activity, Skipping meals";
    public String pittahowtobal = "Cool environments, limit salt intake, regular meal times";

    public String kaphabal = "Good memory, good stamina, stability, compassionate, natural resistance to sickness";
    public String kaphaimbal = "Complacent, lethargic, sinus congestion, allergies, overweight, slow digestion, depression";
    public String kaphadueto = "Excessive rest and sleep, excessive food intake, insufficient exercise, unctuous food, too much sweet or salt intake";
    public String kaphahowtobal = "Vigorous regular exercise, warm light food, warm dry environment, varying routine";

    public String vatastate;
    public String pittastate;
    public String kaphastate;
    public String comments;


    public Diagnose(String vatastate, String pittastate, String kaphastate, String comments)
    {
        this.vatastate = vatastate;
        this.pittastate = pittastate;
        this.kaphastate = kaphastate;
        this.comments = comments;
    }

    public String returnDiagnosis()
    {
        String v= "",p="",k="",t="";

        if(vatastate.equals("Balanced"))
        {
            v = "\n\nBalanced Vata means: "+vatabal+"\n\nMaintain: "+vatahowtobal+"\n";
        }
        else if(vatastate.equals("Imbalanced"))
        {
            v = "\n\nImalanced Vata means: "+vataimbal+"\n\nReasons might include: "+vatadueto+"\n\nMaintain: "+vatahowtobal+"\n";
        }

        if(pittastate.equals("Balanced"))
        {
            p = "\n\nBalanced pitta means: "+pittabal+"\n\nMaintain: "+pittahowtobal+"\n";
        }
        else if(pittastate.equals("Imbalanced"))
        {
            p = "\n\nImalanced pitta means: "+pittaimbal+"\n\nReasons might include: "+pittadueto+"\n\nMaintain: "+pittahowtobal+"\n";
        }

        if(kaphastate.equals("Balanced"))
        {
            k = "\n\nBalanced kapha means: "+kaphabal+"\n\nMaintain: "+kaphahowtobal+"\n";
        }
        else if(kaphastate.equals("Imbalanced"))
        {
            k = "\n\nImalanced kapha means: "+kaphaimbal+"\n\nReasons might include: "+kaphadueto+"\n\nMaintain: "+kaphahowtobal+"\n";
        }

        t = v+p+k+comments;

        return t;
    }

}
