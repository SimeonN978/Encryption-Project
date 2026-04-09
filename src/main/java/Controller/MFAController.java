package Controller;

import Service.MFAService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import spark.Request;
import spark.Response;

import static Service.HTMLRenderer.render;
import static spark.Spark.*;

public class MFAController implements Controller{
    private final MFAService mfaService;

    public MFAController(MFAService mfaService){
        this.mfaService = mfaService;
    }

    @Override
    public void register(){
        // Show mfa page to user
        before();
        get("/mfa", this::showMFAPage);

        // Handle mfa otp Authentication
        post("/verify-otp", this::verifyOTP);
    }

    private Object verifyOTP(Request request, Response response) {
        //TODO
        return null;
    }

    private Object showMFAPage(Request request, Response response){
        response.status(200);
        response.type("text/html");
        return render("/views/mfa.html");
    }
}
