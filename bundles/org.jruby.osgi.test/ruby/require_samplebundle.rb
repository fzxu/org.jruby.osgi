require 'osgibundle:/org.jruby.osgi.test.samplebundle'
class Java::OrgJrubyOsgiTestSamplebundle::MyOtherClass
  def say_hello_as_well
    puts "hello_as_well"
  end
end