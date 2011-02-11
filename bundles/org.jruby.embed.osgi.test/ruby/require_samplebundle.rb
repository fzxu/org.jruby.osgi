require 'osgibundle:/org.jruby.embed.osgi.test.samplebundle'
class Java::OrgJrubyEmbedOsgiTestSamplebundle::MyOtherClass
  def say_hello_as_well
    puts "hello_as_well"
  end
end