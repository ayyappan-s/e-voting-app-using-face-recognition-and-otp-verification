from django.shortcuts import render
from django.contrib.auth.decorators import login_required
import pyrebase
from django.conf import settings
from django.http import JsonResponse
# Create your views here.
@login_required(login_url='login')
def results(request):
    return render(request,'results.html')
def resultsofpolls(request):
    if request.is_ajax():
            firebase=pyrebase.initialize_app(settings.FIREBASE_CONFIG)
            database=firebase.database()
            res= database.child("endedpolls").get().val()
            val={}
            val["k"]=res
            return JsonResponse(val)