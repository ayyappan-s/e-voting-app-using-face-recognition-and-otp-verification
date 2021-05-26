import pyrebase
from django.http import JsonResponse
from django.shortcuts import render
from .forms import Candidate_Form
from django.conf import settings

# Create your views here.
def create_poll(request):
    context={}
    context['form']=Candidate_Form()
    return render(
        request,'create_poll.html',context
    )
def add_candidate(request):
    if request.is_ajax():
        print(request.POST)
        name=request.POST.get('name')
        state=request.POST.get('state')
        district=request.POST.get('district')
        legislative=request.POST.get('legislative')
        candidate_image=request.FILES['candidate_image']
        candidate_symbol=request.FILES['candidate_symbol']
        res={}
        try:
            firebase=pyrebase.initialize_app(settings.FIREBASE_CONFIG)
            storage=firebase.storage()
            storage.child("candidate_image/"+state+"/"+district+"/"+legislative+"/"+name+"/"+"candidate_image"+".jpg").put(candidate_image)
            storage.child("candidate_image/" + state + "/" + district + "/" + legislative + "/" + name + "/" + "candidate_symbol" + ".jpg").put(candidate_symbol)
            candidate_image_url =storage.child("candidate_image/"+state+"/"+district+"/"+legislative+"/"+name+"/"+"candidate_image"+".jpg").get_url(None)
            candidate_symbol_url=storage.child("candidate_image/" + state + "/" + district + "/" + legislative + "/" + name + "/" + "candidate_symbol" + ".jpg").get_url(None)
            database=firebase.database()
            database.child("candidate").child(state).child(district).child(legislative).child(name).set({
                'candidate_image_url':candidate_image_url,
                'candidate_symbol_url':candidate_symbol_url
            })
            res['msg'] = 'success'
            return JsonResponse(res)
        except Exception as e:
            print(e)
            res['msg']='failure'
            return JsonResponse(res)

