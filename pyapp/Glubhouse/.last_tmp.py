#qpy:webapp:Glubhouse
#qpy://localhost:11358/whait4inv

import sys, androidhelper,json
from bottle import *
import bottle
from clubhouse import Clubhouse
	
droid = androidhelper.Android()
bottle.TEMPLATE_PATH.insert(0, '/sdcard/qpython/projects3/Glubhouse/res/templates/')
app = Bottle()


class MyWSGIRefServer(ServerAdapter):
    server = None

    def run(self, handler):
        from wsgiref.simple_server import make_server, WSGIRequestHandler
        if self.quiet:
            class QuietHandler(WSGIRequestHandler):
                def log_request(*args, **kw): pass
            self.options['handler_class'] = QuietHandler
        self.server = make_server(self.host, self.port, handler, **self.options)
        self.server.serve_forever()

    def stop(self):
        #sys.stderr.close()
        import threading 
        threading.Thread(target=self.server.shutdown).start() 
        #self.server.shutdown()
        self.server.server_close() #<--- alternative but causes bad fd exception
        print("# qpyhttpd stop")

######### BUILT-IN ROUTERS ###############
@error(404) 
def error404(error): 
    redirect("/start")


@app.route("/app")
def index():
    return noTemplate('app.html')
    
@app.route("/login")
def login():
    return noTemplate('login.html')

@app.route("/whait4inv")
def whaitForInvite():
    return noTemplate('whait4invite.html')

@app.route("/start")
def startPoint():
    cred = GetCredential()
    if (cred[1]["extras"]):
        token = str(cred[1]["extras"]["user_token"])
        uid = 	str(cred[1]["extras"]["user_id"])
        udev =	str(cred[1]["extras"]["user_device"])
        if (token and uid and udev):    
            redirect("/app")
        else:
            redirect("/login")
    


    

@app.route("/assets/<filename:path>")
def static_assets(filename):
    return static_file(filename, root='/sdcard/qpython/projects3/Glubhouse/res/static')


@app.route("/__exit")
def ExitfApp():
    global server
    server.stop()

#########    API ROUTES  ################

@app.route("/api/sendCode/<phone:re:\+[0-9]{3,}>")
def APIsendCode(phone):
    client = getCClient()
    result = client.start_phone_number_auth(phone)
    response.content_type = "application/json"
    return json.dumps(result)
    
@app.route("/api/sendCode/<phone:re:\+[0-9]{3,}>/<code:int>")
def APIcodeVerify(phone,code):
    client = getCClient()
    result = client.complete_phone_number_auth(phone, code)
    if not result['success']:
        abort(401,"bad code")
        return
    else:
        try:
            user_id = str(result['user_profile']['user_id'])
            user_token = str(result['auth_token'])
            user_device = str(client.HEADERS.get("CH-DeviceId"))                 
            SetCredential(user_device,user_token,user_id)
            
            response.content_type = "application/json"
            return json.dumps(result)
        except:
            abort(401,"old code")

@app.route("/loginout")
def loginOut():
    SetCredential("","","")
    redirect("/login")
    
@app.route("/api/myt")
def MyToken():
    cred = GetCredential()
    if (cred[1]["extras"]):
        return json.dumps(cred[1]["extras"])
    else:
        redirect("/login")
        
@app.route("/api/me")
def MyToken():
    cred = request.json()
    client = getCClient(
    	cred["utoken"],
    	cred["uid"],
    	cred["udevice"])
    return str(client.me())
    
    
@app.route("/api/invite/<phone>")
def InviteUser(phone):
    cred = GetCredential()
    token = cred[1]["extras"]["user_token"]
    uid = 	cred[1]["extras"]["user_id"]
    udev =	cred[1]["extras"]["user_device"]
        
    client = getCClient(
    	token,
    	uid,
    	udev)
    return str(client.invite_from_waitlist("517320708"))


#########    FUNCTIONS   ################

def IsAppInstalled():
    if droid.getLaunchableApplications()[1]["Glubhouse"]:
        return True
    else:
        return False

    
def GetCredential():
    extras = {
    	    "test":"13trst2215",
    	}
    return droid.startActivityForResult(
    	"getcred",
    	None,None,json.loads(json.dumps(extras)),
     "io.github.gornostay25.glubhouse",
     "io.github.gornostay25.glubhouse.API")

def SetCredential(udevice,utoken,uid):
    
    extras = {
        	"test":"13trst2215",
    	    "udevice":str(udevice),
    	    "uid":str(uid),
    	    "utoken":str(utoken)
    	}
    
    Jextras = json.loads(json.dumps(extras))
    
    print(droid.startActivityForResult(
    	"setcred",
    	None,None,Jextras,
    	"io.github.gornostay25.glubhouse",
    	"io.github.gornostay25.glubhouse.API"
    	))


def getCClient(utoken=None,uid=None,udevice=None):
    if utoken and uid and udevice:
        return Clubhouse(
        user_id=uid,
        user_token=utoken,
        user_device=udevice
    )
    else:
        return Clubhouse()


def noRepeat():
    me = os.popen("ps -o args -p "+str(os.getpid())).read().split("\n")[1:-1][0]
    for x in os.popen("ps -o pid,args -a").read().split("\n")[1:-1]:
        x = x.split(' ',1)
        print(x[1] == me)
        if(x[0] != str(os.getpid()) and x[1] == me):
            droid.makeToast("anather")
            sys.exit(0)
def noTemplate(file):
    with open("/sdcard/qpython/projects3/Glubhouse/res/templates/"+file,"r") as f:
        return f.read()


if __name__ == '__main__':
    noRepeat()
    if not IsAppInstalled():
        droid.view("https://gornostay25.github.io/glubhouse/")
        sys.exit(0)
    try:
        #droid.wakeLockAcquirePartial()
        server = MyWSGIRefServer(host="0.0.0.0", port="11358")
        app.run(server=server,reloader=False)
        #cred = getCClient()
        #print(GetCredential())
           
    except Exception as e:
        print(str(e))
        droid.makeToast(str(e))
        os.system("pkill python")
        sys.exit(1)