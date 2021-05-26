from django.shortcuts import render
import pyrebase
from django.views.decorators.csrf import csrf_exempt
from django.conf import settings
from django.http import JsonResponse
import cv2
import numpy
from PIL import Image
import requests
import face_recognition
from django.contrib.staticfiles.storage import staticfiles_storage
# Create your views here.
@csrf_exempt
def check_user(request):
    firebase=pyrebase.initialize_app(settings.FIREBASE_CONFIG)
    database=firebase.database()
    state=request.POST.get("state")
    district=request.POST.get("district")
    legislative=request.POST.get("legislative")
    phoneNumber=request.POST.get("phoneNumber")

    val=database.child("voters").child(state).child(district).child(legislative).child(phoneNumber).get().val()
    print(val)
    if val==None:
        print("if running")
        return JsonResponse({'res':'NOT FOUND'})
    else:
        return JsonResponse({'res':'FOUND'})
@csrf_exempt
def face_recognizer(request):
    firebase=pyrebase.initialize_app(settings.FIREBASE_CONFIG)
    database=firebase.database()
    response={}
    image=request.FILES['voter_image'].read()
    npimg=numpy.fromstring(image,numpy.uint8)
    img=cv2.imdecode(npimg,cv2.IMREAD_COLOR)

    print(type(image))
    state = request.POST.get("state")
    district = request.POST.get("district")
    legislative = request.POST.get("legislative")
    phoneNumber = request.POST.get("phoneNumber")
    print("IMFROMHERE")
    print(state, district, legislative, phoneNumber)
    print("IMFROMHERE")
    npimg = numpy.fromstring(image, numpy.uint8)
    img = cv2.imdecode(npimg, cv2.IMREAD_COLOR)

    face_cascade = cv2.CascadeClassifier(
        staticfiles_storage.path('haarcascades/haarcascade_frontalface_default.xml'))
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    faces = face_cascade.detectMultiScale(gray, 1.3, 5)

    print(faces)
    for (x, y, w, h) in faces:
        cv2.rectangle(img, (x, y), (x + w, y + h), (255, 0, 0), 2)

    cv2.imwrite("detected.jpg", img)
    count = len(faces)
    print(count)
    if count == 1:
        k=database.child("voters").child(state).child(district).child(legislative).child(phoneNumber).get().val()
        im=Image.open(requests.get(k['image'],stream=True).raw).convert('RGB')
        downloadedImage=cv2.cvtColor(numpy.array(im),cv2.COLOR_RGB2BGR)
        downloadedEncodings=face_recognition.face_encodings(downloadedImage)[0]
        uploadedImage=cv2.cvtColor(numpy.array(img),cv2.COLOR_RGB2BGR)
        uploadedEncodings=face_recognition.face_encodings(uploadedImage)[0]
        results=face_recognition.compare_faces([downloadedEncodings],uploadedEncodings)
        if(results[0]==True):
            response['msg']='OK'
            response['Found']='TRUE'
        else:
            response['msg']='NOT OK'
            response['FOUND']='FALSE'
    elif count > 1:
        response['msg'] = 'More Than One Face Was Detected.! Invalid Image.'
        response['Found']='FALSE'
    elif count == 0:
        response['msg'] = 'No Face Was Detected'
        response['Found']='FALSE'
    print(response)
    return JsonResponse(response)
@csrf_exempt
def addvote(request):
    try:
        state = request.POST.get("state")
        district = request.POST.get("district")
        legislative = request.POST.get("legislative")
        candidate_name=request.POST.get("candidate_name")
        phoneNumber=request.POST.get("phoneNumber")
        firebase=pyrebase.initialize_app(settings.FIREBASE_CONFIG)
        database=firebase.database()
        prev_val=database.child("runningpolls").child(state).child(district).child(legislative).child(candidate_name).get().val()
        database.child("runningpolls").child(state).child(district).child(legislative).update({candidate_name:prev_val+1})
        database.child("voters").child(state).child(district).child(legislative).child(phoneNumber).update({'voted':1})
        return JsonResponse({'res':'success'})
    except:
        return JsonResponse({"res":"failure"})
@csrf_exempt
def voter_confirmation(request):
    try:
        firebase=pyrebase.initialize_app(settings.FIREBASE_CONFIG)
        database=firebase.database()
        state=request.POST.get("state")
        district=request.POST.get("district")
        legislative=request.POST.get("legislative")
        phoneNumber=request.POST.get("phoneNumber")

        val=database.child("voters").child(state).child(district).child(legislative).child(phoneNumber).get().val()
        print(val)
        if val==None:
            print("if running")
            return JsonResponse({'res':'NOT FOUND'})
        else:
            val=database.child("voters").child(state).child(district).child(legislative).child(phoneNumber).child("voted").get().val()
            if val==1:
                return JsonResponse({'res':'NOTVALID'})
            else:
                return JsonResponse({'res':'VALID'})
    except:
        return JsonResponse({"res":"Error"})
