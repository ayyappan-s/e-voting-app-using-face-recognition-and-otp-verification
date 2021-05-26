from django.contrib.auth.decorators import login_required
from django.http import JsonResponse
from django.shortcuts import render
from .forms import *
import pyrebase
from django.conf import settings
from create_poll.forms import Candidate_Form
from django.http import HttpResponse
from django.core.serializers import serialize
from django.contrib.staticfiles.storage import staticfiles_storage
import pyrebase
from pyrebase.pyrebase import storage
import firebase_admin
from firebase_admin import storage as admin_storage, credentials, firestore
from firebase_admin import credentials
from django.views.decorators.csrf import csrf_exempt
# Create your views here.
@login_required(login_url='login')
def dashboard(request):
    context={}
    context['forms']=dashboard_forms()

    return render(request,'dashboard.html',context)
@csrf_exempt
def availability_checker(request):
        state=request.POST.get('state')
        district=request.POST.get('district')
        legislative=request.POST.get('legislative')
        print(state,district,legislative)
        firebase=pyrebase.initialize_app(settings.FIREBASE_CONFIG)
        database=firebase.database()
        num=database.child("voters").child(state).child(district).child(legislative).get().val()
        for i in num.keys():
            database.child("voters").child(state).child(district).child(legislative).child(i).update({"voted":0})
        res= database.child("candidate").child(state).child(district).child(legislative).get().val()
        response={}
        print(res)
        cur=[]
        val=database.child("candidate").child(state).child(district).child(legislative).get().val().keys()
        key=list(val)
        for i in res.keys():
            cur.append(i)
            response[i]=res[i]
            print(i)
        response['k']=cur
        response['keys']=key
        print("length",len(res))
        response['len']=len(res)
        return JsonResponse(response)
def startpoll(request):
    state=request.POST.get('state')
    district=request.POST.get('district')
    legislative=request.POST.get('legislative')
    print(state,district,legislative)
    firebase=pyrebase.initialize_app(settings.FIREBASE_CONFIG)
    database=firebase.database()

    num=database.child("voters").child(state).child(district).child(legislative).get().val()
    for i in num.keys():
        database.child("voters").child(state).child(district).child(legislative).child(i).update({"voted":0})
    cand={}
    res= database.child("candidate").child(state).child(district).child(legislative).get().val()
    for i in res.keys():
            cand[i]=0
    database.child("runningpolls").child(state).child(district).child(legislative).set(cand)
    return JsonResponse({"res":"success"})
def state_adder(request):
        STATE_CHOICES=[('Andaman Nicobar', 'Andaman Nicobar'),
 ('Andhra Pradesh', 'Andhra Pradesh'),
 ('Arunachal Pradesh', 'Arunachal Pradesh'),
 ('Assam', 'Assam'),
 ('Bihar', 'Bihar'),
 ('Chandigarh', 'Chandigarh'),
 ('Chhattisgarh', 'Chhattisgarh'),
 ('Dadra Nagar Haveli', 'Dadra Nagar Haveli'),
 ('Daman Diu', 'Daman Diu'),
 ('Delhi', 'Delhi'),
 ('Goa', 'Goa'),
 ('Gujarat', 'Gujarat'),
 ('Haryana', 'Haryana'),
 ('Himachal Pradesh', 'Himachal Pradesh'),
 ('Jammu Kashmir', 'Jammu Kashmir'),
 ('Jharkhand', 'Jharkhand'),
 ('Karnataka', 'Karnataka'),
 ('Kerala', 'Kerala'),
 ('Ladakh', 'Ladakh'),
 ('Lakshadweep', 'Lakshadweep'),
 ('Madhya Pradesh', 'Madhya Pradesh'),
 ('Maharashtra', 'Maharashtra'),
 ('Manipur', 'Manipur'),
 ('Meghalaya', 'Meghalaya'),
 ('Mizoram', 'Mizoram'),
 ('Nagaland', 'Nagaland'),
 ('Odisha', 'Odisha'),
 ('Puducherry', 'Puducherry'),
 ('Punjab', 'Punjab'),
 ('Rajasthan', 'Rajasthan'),
 ('Sikkim', 'Sikkim'),
 ('Tamil Nadu', 'Tamil Nadu'),
 ('Telangana', 'Telangana'),
 ('Tripura', 'Tripura'),
 ('Uttarakhand', 'Uttarakhand'),
 ('Uttar Pradesh', 'Uttar Pradesh'),
 ('West Bengal', 'West Bengal')]
        return JsonResponse({'state':STATE_CHOICES})
def deleteCandidate(request):
    if request.is_ajax():

            state = request.POST.get('state')
            district=request.POST.get('district')
            legislative=request.POST.get('legislative')
            name=request.POST.get("name")
            m ={}
            m["storageBucket"]='online-voting-system-bd333.appspot.com'
            try:
               app = firebase_admin.get_app()
            except ValueError as e:
              cred = credentials.Certificate(staticfiles_storage.path("json/online-voting-system-bd333-firebase-adminsdk-be8tf-86a8e22480.json"))
              app=firebase_admin.initialize_app(cred,m)
            firebase=pyrebase.initialize_app(settings.FIREBASE_CONFIG)
            database=firebase.database()
            database.child("candidate").child(state).child(district).child(legislative).child(name).remove()



            bucket = admin_storage.bucket()
            blob = bucket.blob("candidate_image/"+state+"/"+district+"/"+legislative+"/"+name+"/candidate_symbol.jpg")
            print(blob)
            blob.delete()
            blob = bucket.blob("candidate_image/"+state+"/"+district+"/"+legislative+"/"+name+"/candidate_image.jpg")
            print(blob)
            blob.delete()
            return JsonResponse({"result":"success"})
@csrf_exempt
def viewactivepoll(request):
            firebase=pyrebase.initialize_app(settings.FIREBASE_CONFIG)
            database=firebase.database()
            res= database.child("runningpolls").get().val()
            val={}
            val["k"]=res
            return JsonResponse(val)
@csrf_exempt
def check_poll_status(request):
        firebase=pyrebase.initialize_app(settings.FIREBASE_CONFIG)
        database=firebase.database()
        state = request.POST.get('state')
        district=request.POST.get('district')
        legislative=request.POST.get('legislative')
        print(state,district,legislative)
        val=database.child("runningpolls").child(state).child(district).child(legislative).get().val()
        data={}
        if val==None:
            data["msg"]='notrunning'
        else:
            data["msg"]='running'

        return JsonResponse(data)
def endpoll(request):
        try:
            firebase=pyrebase.initialize_app(settings.FIREBASE_CONFIG)
            database=firebase.database()
            state = request.POST.get('state')
            district=request.POST.get('district')
            legislative=request.POST.get('legislative')
            v= database.child("runningpolls").child(state).child(district).child(legislative).get().val()
            database.child("endedpolls").child(state).child(district).child(legislative).set(v)
            database.child("runningpolls").child(state).child(district).child(legislative).remove()

            return JsonResponse({'msg':'success'})
        except:
            return JsonResponse({'msg':'failure'})
def viewendedpoll(request):
        if request.is_ajax():
                firebase=pyrebase.initialize_app(settings.FIREBASE_CONFIG)
                database=firebase.database()
                res= database.child("endedpolls").get().val()
                val={}
                val["k"]=res
                return JsonResponse(val)
