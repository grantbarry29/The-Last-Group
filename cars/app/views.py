from django.shortcuts import render


# Create your views here.
from django.http import JsonResponse, HttpResponse
from django.views.decorators.csrf import csrf_exempt
import json

@csrf_exempt
def postchatt(request):
    if request.method != 'POST':
        return HttpResponse(status=404)
    response = {}
    response['p'] = ['Replace Me', 'DUMMY RESPONSE'] # **DUMMY response!**
    return JsonResponse(response)


def getchatts(request):
    if request.method != 'GET':
        return HttpResponse(status=404)
    response = {}
    response['chatts'] = ['Replace Me', 'DUMMY RESPONSE'] # **DUMMY response!**
    return JsonResponse(response)
